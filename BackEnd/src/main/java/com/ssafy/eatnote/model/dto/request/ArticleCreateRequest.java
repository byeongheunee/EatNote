package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시글 생성 요청 DTO")
public class ArticleCreateRequest {

    @Schema(description = "게시글 제목", example = "오늘의 식단 공유합니다!")
    private String title;

    @Schema(description = "게시글 내용", example = "닭가슴살과 고구마를 먹었어요.")
    private String content;

    @Schema(description = "소속 게시판 ID", example = "1")
    private Integer boardId;
}
