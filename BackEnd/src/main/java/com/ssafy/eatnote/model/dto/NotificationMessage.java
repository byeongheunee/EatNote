package com.ssafy.eatnote.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private Long receiverId;
    private String typeCode;
    private String content;
    private Long relatedMealId;
}