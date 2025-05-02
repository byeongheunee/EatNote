package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "좋아요/싫어요 타입 DTO")
public class LikeType {

    @Schema(description = "타입 코드", example = "LIKE")
    private String typeCode;

    @Schema(description = "설명", example = "좋아요")
    private String description;
}
