package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 첨부파일 DTO")
public class ArticleFile {

    @Schema(description = "파일 ID", example = "1001")
    private Long fileId;

    @Schema(description = "게시글 ID", example = "101")
    private Integer articleId;

    @Schema(description = "원본 파일명", example = "recipe.pdf")
    private String originalName;

    @Schema(description = "서버 저장 파일명", example = "c3f45b9c-1234-4c9f-bf1a-2a6d63f15c34.pdf")
    private String savedName;

    @Schema(description = "파일 경로 또는 URL", example = "https://fitbob.s3.amazonaws.com/files/c3f45b9c-...pdf")
    private String filePath;

    @Schema(description = "파일 MIME 타입", example = "application/pdf")
    private String fileType;

    @Schema(description = "업로드 시각", example = "2025-05-02T14:30:00")
    private LocalDateTime uploadedAt;
}
