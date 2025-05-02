package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 목표 코드 DTO")
public class GoalType {

    @Schema(description = "목표 코드", example = "감량")
    private String goalCode;

    @Schema(description = "목표 설명", example = "체중 감량")
    private String description;
}
