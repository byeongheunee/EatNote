package com.ssafy.eatnote.model.dto.response;

import java.util.List;

import com.ssafy.eatnote.model.dto.request.ChatGPTRequest;

import lombok.*;

//ChatGPTResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGPTResponse {
 private List<Choice> choices;

 @Data
 public static class Choice {
     private int index;
     private ChatGPTRequest.Message message;
 }
}
