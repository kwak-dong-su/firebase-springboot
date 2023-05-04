package com.example.api.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fcm")
@CrossOrigin(origins = "*")
public class FcmController {

    @PostMapping(value = "/send", produces = "application/json")
    public String send(@RequestBody String content) throws URISyntaxException, IOException {

        GoogleCredentials googleCredentials =
                GoogleCredentials.fromStream(new ClassPathResource("private_key.json").getInputStream())
                        .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();

        String accessToken = googleCredentials.getAccessToken().getTokenValue();

        HttpPost httpPost = new HttpPost();
        httpPost.addHeader("Authorization", "Bearer "+accessToken);
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json; UTF-8");

        httpPost.setURI(new URIBuilder
                ("https://fcm.googleapis.com/v1/projects/test-db-d94f0/messages:send").build()
        );

        Map<String, Object> body= new HashMap<>();
        Gson gson = new Gson();
//        body.put("");
//        httpPost.setEntity();

        return accessToken;
    }
}
