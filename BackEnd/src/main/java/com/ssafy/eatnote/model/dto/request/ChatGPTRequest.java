package com.ssafy.eatnote.model.dto.request;

// ChatGPTRequest.java


import java.util.List;
import lombok.*;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTRequest {
    private String model; // ex) "gpt-3.5-turbo"
    private List<Message> messages;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;    // "user", "system", "assistant"
        private String content;
    }
}
