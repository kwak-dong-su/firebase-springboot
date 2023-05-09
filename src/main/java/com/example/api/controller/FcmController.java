package com.example.api.controller;

import com.example.api.config.request.SendMessageRequest;
import com.google.api.services.storage.Storage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import com.google.firebase.messaging.FirebaseMessaging;

@RestController
@RequestMapping("/fcm")
@CrossOrigin(origins = "*")
public class FcmController {

    @PostMapping(value = "/send-rest", produces = "application/json")
    public String sendRest(@RequestBody SendMessageRequest request) throws URISyntaxException, IOException {

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


        JsonObject notification = new JsonObject();
        notification.addProperty("body", request.getContent());
        notification.addProperty("title", request.getTitle());

        JsonObject message = new JsonObject();
        message.add("notification", notification);

        message.addProperty("token", request.getToken());

        JsonObject body = new JsonObject();
        body.add("message", message);

        httpPost.setEntity(new StringEntity(body.toString()));

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);

        String result = EntityUtils.toString(response.getEntity());

        System.out.println(result);

        return result;
    }

    @PostMapping(value = "/send-admin-sdk", produces = "application/json")
    public String sendAdminSdk(@RequestBody SendMessageRequest request) throws FirebaseMessagingException {

        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getContent())
                .build();

        Message message = Message.builder()
                .putData("score", "850")
                .putData("time", "2:45")
                .setToken(request.getToken())
                .setNotification(notification)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println(response);
        return response;
    }

    @PostMapping(value = "/subscribe", produces = "application/json")
    public String subscribe(@RequestBody List<String> tokenList) throws FirebaseMessagingException {

        TopicManagementResponse topicResponse = FirebaseMessaging.getInstance()
                .subscribeToTopic(tokenList, "news");
        System.out.println("success subscribe: "+topicResponse.getSuccessCount());
        System.out.println("fail subscribe: "+topicResponse.getFailureCount());
        return topicResponse.toString();
    }

    @PostMapping(value = "/unsubscribe", produces = "application/json")
    public String unsubscribe(@RequestBody List<String> tokenList) throws FirebaseMessagingException {

        TopicManagementResponse topicResponse = FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(tokenList, "news");
        System.out.println("success unsubscribe: "+topicResponse.getSuccessCount());
        System.out.println("fail unsubscribe: "+topicResponse.getFailureCount());
        return topicResponse.toString();
    }

    @PostMapping(value = "/send-admin-sdk-all", produces = "application/json")
    public String sendAllPlatforms(@RequestBody SendMessageRequest request) throws FirebaseMessagingException {

        // WebPushNotification, AndroidNotification, Aps 세팅 시 기존 Notification 메세지를 덮어 씀
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getContent())
                .build();

        WebpushNotification wpn = WebpushNotification.builder()
                .setTitle("web push notification")
                .setBody("push for web")
                .build();

        AndroidNotification adn = AndroidNotification.builder()
                .setTitle("android notification")
                .setBody("push for android")
                .build();

        Aps aps = Aps.builder()
                .setAlert("iOS notification")
                .build();

        Message message = Message.builder()
                .setToken(request.getToken())
                .setNotification(notification)
                .setTopic("news")
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(wpn)
                        .build()
                )
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(adn)
                        .build()
                )
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(aps)
                        .build()
                )
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println(response);

        return response;
    }

    @GetMapping(value = "/json-test", produces = "application/json")
    public String jsonTest(){
        Gson gson = new Gson();

        JsonObject notification = new JsonObject();
        notification.addProperty("body", "바로 구매하세요!");
        notification.addProperty("title", "매진 임박");

        JsonObject message = new JsonObject();
        message.add("notification", notification);

        message.addProperty("token", "123");

        JsonObject body = new JsonObject();
        body.add("message", message);

        System.out.println(body);

        return null;
    }

    @PostMapping(value = "/dry-run", produces = "application/json")
    public String dryRun(@RequestBody SendMessageRequest request) throws FirebaseMessagingException {

        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getContent())
                .build();

        Message message = Message.builder()
                .setToken(request.getToken())
                .setNotification(notification)
                .build();

        String response = FirebaseMessaging.getInstance().send(message, true);
        System.out.println(response);
        return response;
    }
}
