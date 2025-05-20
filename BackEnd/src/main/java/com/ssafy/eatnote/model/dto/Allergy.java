package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "알레르기 항목 DTO")
public class Allergy {

    @Schema(description = "알레르기 ID", example = "1")
    private Integer allergyId;

    @Schema(description = "알레르기 이름", example = "땅콩")
    private String name;

    @Schema(description = "카테고리", example = "견과류")
    private String category;
}
