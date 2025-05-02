package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "콘텐츠 타입 DTO (게시글 / 댓글 구분)")
public class ContentType {

    @Schema(description = "콘텐츠 타입 코드", example = "ARTICLE")
    private String typeCode;

    @Schema(description = "콘텐츠 타입 설명", example = "게시글")
    private String description;
}
