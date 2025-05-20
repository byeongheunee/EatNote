package com.ssafy.eatnote.model.dto.request;

import lombok.Data;

@Data
public class LikeRequest {
    private String contentType;  // 'ARTICLE', 'MEAL', 'COMMENT' 등
    private Long contentId;      // 대상 ID
    private String likeType;     // 'LIKE' or 'DISLIKE'
}
