package org.salex.raspberry.workshop.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Der Photographer kapselt den Zugriff auf die Camera und
 * verwaltet einen Cache von Photos.
 */
@Component
public class Photographer {
    private static final Logger LOG = LoggerFactory.getLogger(Photographer.class);
    private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
    private static final String PREFIX = "workshop-";
    private static final String SUFFIX = ".jpg";

    private final Map<String, String> cache = new HashMap<>();
    private final String script;
    private final File cacheDirectory;

    private final int defaultPhotoWidht;
    private final int defaultPhotoHeight;

    public Photographer(
        @Value("${org.salex.raspberry.camera.script}") String script,
        @Value("${org.salex.raspberry.photo.cache}") String cacheDirectory,
        @Value("${org.salex.raspberry.workshop.blog.picture.defaultWidth}") int defaultPhotoWidth,
        @Value("${org.salex.raspberry.workshop.blog.picture.defaultHeight}") int defaultPhotoHeight) {
        this.defaultPhotoWidht = defaultPhotoWidth;
        this.defaultPhotoHeight = defaultPhotoHeight;
        this.script = script;
        this.cacheDirectory = new File(cacheDirectory);
        if(!this.cacheDirectory.exists()) {
            LOG.error("Photo cache directory does not exist: " + this.cacheDirectory.getAbsolutePath());
        } else if(!this.cacheDirectory.isDirectory()) {
            LOG.error("Photo cache ist not a directory but a file: " + this.cacheDirectory.getAbsolutePath());
        } else {
            for(File each : this.cacheDirectory.listFiles()) {
                cache.put(each.getName(), each.getAbsolutePath());
            }
        }
    }

    /**
     * Nimmt ein neues Foto über die Kamera auf.
     * @param width Die Breite des aufzunehmenden Fotos
     * @param height Die Höhe des aufzunehmenden Fotos
     * @return Ein Input-Stream, um das Foto zu lesen.
     */
    public InputStream takePhoto(int width, int height) throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(script, "-w", Integer.toString(width), "-h", Integer.toString(height), "-o", "-");
        final Process process = builder.start();
        return process.getInputStream();
    }

    /**
     * Nimmt ein neues Foto im Standardformat für den Blog auf
     * und analysiert es dahingehend, ob Licht in der Werkstatt an war.
     * Falls ja, wird es im Cache gespeichert.
     */
    public void takePhotoForCache() throws IOException, InterruptedException {
        final String filename = PREFIX + FORMATER.format(new Date()) + SUFFIX;
        final File file = new File(this.cacheDirectory, filename);
        final BufferedImage image = takePhoto(this.defaultPhotoWidht, this.defaultPhotoHeight, file);
        if(isWorkshopLighted(image)) {
            this.cache.put(file.getName(), file.getAbsolutePath());
        } else {
            file.delete();
        }
    }

    private BufferedImage takePhoto(int width, int height, File file) throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder(script, "-w", Integer.toString(width), "-h", Integer.toString(height), "-o", file.getAbsolutePath());
        final Process process = builder.start();
        final int result = process.waitFor();
        if(result == 0) {
            return ImageIO.read(file);
        } else {
            throw new RuntimeException("Error taking a photo, camera script returned " + result);
        }
    }

    private boolean isWorkshopLighted(BufferedImage image ) throws IOException {
        if(image != null) {
            final Color pixel = new Color(image.getRGB(420,20));
            return pixel.getRed() > 220 && pixel.getGreen() > 220 && pixel.getBlue() > 220;
        } else {
            return false;
        }
    }


    public Map<String, String> getPhotos() {
        return cache;
    }

    public void deletePhotos(Collection<String> filenames) {
        for(String filename : filenames) {
            deletePhoto(filename);
        }
    }

    public void deletePhoto(String filename) {
        synchronized(this.cache) {
            (new File(this.cache.get(filename))).delete();
            this.cache.remove(filename);
        }
    }

}
