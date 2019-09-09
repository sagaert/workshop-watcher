package org.salex.raspberry.workshop.photo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Der Photographer kapselt den Zugriff auf die Camera und
 * verwaltet einen Cache von Photos.
 */
@Component
public class Photographer {
    private final String script;

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
     * Nimmt ein neues Foto über die Kamera auf und speichert es ab.
     * @param width Die Breite des aufzunehmenden Fotos
     * @param height Die Höhe des aufzunehmenden Fotos
     * @param filepath Der Pfad der Datei, in dem das Foto gespeichert werden soll.
     * @return Das aufgenommene Foto.
     */
    public BufferedImage takePhoto(int width, int height, String filepath) throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder(script, "-w", Integer.toString(width), "-h", Integer.toString(height), "-o", filepath);
        final Process process = builder.start();
        final int result = process.waitFor();
        if(result == 0) {
            final File file = new File(filepath);
            return ImageIO.read(file);:q::q!
                :
        } else {
            throw new RuntimeException("Error taking a photo, camera script returned " + result);
        }
    }

    public Photographer(@Value("${org.salex.raspberry.camera.script}") String script) {
        this.script = script;
    }
}
