package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 수정 요청 DTO")
public class ArticleUpdateRequest {

    @Schema(description = "게시글 제목", example = "수정된 제목입니다.")
    private String title;

    @Schema(description = "게시글 내용", example = "수정된 내용입니다.")
    private String content;

    @Schema(description = "삭제할 첨부파일 ID 목록")
    private List<Long> deleteFileIds;
}
