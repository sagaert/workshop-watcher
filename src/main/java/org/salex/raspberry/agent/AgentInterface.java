package org.salex.raspberry.agent;

import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AgentInterface {


    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getData() {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("Vorname", "Sascha");
        data.put("Nachname", "Gärtner");
        return data;
    }

    @GetMapping(value="/photo", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getPhoto() throws IOException, FailedToRunRaspistillException, InterruptedException {
        RPiCamera camera = new RPiCamera();
        BufferedImage image = camera.takeBufferedStill(800, 600);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
