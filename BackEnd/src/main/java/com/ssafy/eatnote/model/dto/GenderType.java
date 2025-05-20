package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "성별 코드 DTO")
public class GenderType {

    @Schema(description = "성별 코드", example = "M")
    private String genderCode;

    @Schema(description = "성별 라벨", example = "남성")
    private String label;
}
