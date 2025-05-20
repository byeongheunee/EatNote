package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공통 코드 DTO")
public class CommonCode {

    @Schema(description = "코드값", example = "M")
    private String code;

    @Schema(description = "라벨/설명", example = "남성")
    private String label;
}
