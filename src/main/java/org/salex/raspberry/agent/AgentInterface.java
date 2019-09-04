package org.salex.raspberry.agent;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public byte[] getPhoto() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("me.png");
        return IOUtils.toByteArray(in);
    }
}
