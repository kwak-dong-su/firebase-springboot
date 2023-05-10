package com.example.api.request;

import lombok.Data;

@Data
public class SendMessageRequest {
    String title;
    String content;
    String token;
}
