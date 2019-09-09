package org.salex.raspberry.workshop.photo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

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
     * @return Das aufgenommene Foto.
     */
    public BufferedImage takePhoto(int width, int height) throws IOException, InterruptedException {
        final ProcessBuilder builder = new ProcessBuilder(script, "-w", Integer.toString(width), "-h", Integer.toString(height), "-o", "-");
        final Process process = builder.start();
        final int result = process.waitFor();
        if(result == 0) {
            return ImageIO.read(process.getInputStream());
        } else {
            throw new RuntimeException("Error taking a photo, camera script returned " + result);
        }
    }

    public Photographer(@Value("org.salex.raspberry.camera.script") String script) {
        this.script = script;
    }
}
