package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 유형 코드 DTO")
public class UserType {

    @Schema(description = "사용자 유형 코드", example = "1")
    private Integer typeCode;

    @Schema(description = "사용자 유형 라벨", example = "트레이너")
    private String label;
}
