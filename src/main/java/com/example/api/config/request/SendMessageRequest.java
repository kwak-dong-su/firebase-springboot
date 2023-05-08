package com.example.api.config.request;

import lombok.Data;

@Data
public class SendMessageRequest {
    String title;
    String content;
    String token;
}
