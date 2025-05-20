package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 첨부 이미지 응답 DTO")
public class ArticleFileResponse {

    @Schema(description = "파일 ID", example = "301")
    private Long fileId;

    @Schema(description = "원본 파일명", example = "salad.jpg")
    private String originalName;

    @Schema(description = "저장된 파일 경로", example = "/uploads/article/uuid_salad.jpg")
    private String filePath;

    @Schema(description = "파일 MIME 타입", example = "image/jpeg")
    private String fileType;
}
