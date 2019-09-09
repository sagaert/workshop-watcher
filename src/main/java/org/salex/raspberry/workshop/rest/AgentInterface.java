package org.salex.raspberry.workshop.rest;

import org.apache.commons.io.IOUtils;
import org.salex.raspberry.workshop.photo.Photographer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AgentInterface {
    private static Logger LOG = LoggerFactory.getLogger(AgentInterface.class);

    private final Photographer photographer;

    public AgentInterface(Photographer photographer) {
        this.photographer = photographer;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getData() {
        LOG.info("Data requestet");
        final Map<String, String> data = new HashMap<String, String>();
        data.put("Vorname", "Sascha");
        data.put("Nachname", "Gärtner");
        return data;
    }

    @GetMapping(value="/photo", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getPhoto() throws IOException, InterruptedException {
        InputStream image = photographer.takePhoto(800, 600);
        return IOUtils.toByteArray(image);
    }
}
