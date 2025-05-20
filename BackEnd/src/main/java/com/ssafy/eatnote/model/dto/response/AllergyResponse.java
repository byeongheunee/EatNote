package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "알레르기 응답 DTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AllergyResponse {
    @Schema(description = "알레르기 ID", example = "1")
    private int allergyId;

    @Schema(description = "알레르기명", example = "땅콩 알레르기")
    private String name;

    @Schema(description = "카테고리", example = "견과류")
    private String category;
}