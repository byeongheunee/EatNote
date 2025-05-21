package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "좋아요/싫어요 기록 DTO")
public class ContentLike {

    @Schema(description = "좋아요 기록 ID", example = "10001")
    private Long contentLikeId;

    @Schema(description = "사용자 ID", example = "3")
    private Long userId;

    @Schema(description = "콘텐츠 타입 (ARTICLE or COMMENT or MEAL)", example = "ARTICLE")
    private String contentType;

    @Schema(description = "콘텐츠 ID (게시글 ID 또는 댓글 ID)", example = "101")
    private Integer contentId;

    @Schema(description = "좋아요 유형 (LIKE or DISLIKE)", example = "LIKE")
    private String likeType;

    @Schema(description = "기록 생성 시간", example = "2025-05-02T14:55:00")
    private LocalDateTime createdAt;
}
