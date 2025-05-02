package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 DTO")
public class Article {

    @Schema(description = "게시글 ID", example = "101")
    private Integer articleId;

    @Schema(description = "게시글 제목", example = "오늘의 식단 공유합니다!")
    private String title;

    @Schema(description = "게시글 내용", example = "닭가슴살과 고구마를 먹었어요.")
    private String content;

    @Schema(description = "소속 게시판 ID", example = "1")
    private Integer boardId;

    @Schema(description = "작성자 사용자 ID", example = "5")
    private Long userId;

    @Schema(description = "대표 이미지 URL", example = "https://fitbob.s3.amazonaws.com/thumbnails/article101.jpg")
    private String imageUrl;

    @Schema(description = "조회수", example = "35")
    private Integer viewCnt;

    @Schema(description = "작성일시", example = "2025-05-02T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-05-02T14:00:00")
    private LocalDateTime modifiedAt;
}
