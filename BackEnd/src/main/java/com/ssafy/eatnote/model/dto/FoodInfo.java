package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "음식 정보 DTO (DB에서 조회한 영양 성분 포함)")
public class FoodInfo {

    @Schema(description = "음식 코드", example = "05012001")
    private String foodCode;

    @Schema(description = "음식 이름", example = "고구마")
    private String name;

    @Schema(description = "1인분 기준 에너지 (kcal)", example = "165.2")
    private Float calories;

    @Schema(description = "탄수화물 (g)", example = "38.3")
    private Float carbohydrates;

    @Schema(description = "단백질 (g)", example = "2.1")
    private Float protein;

    @Schema(description = "지방 (g)", example = "0.2")
    private Float fat;
    
    @Schema(description = "당류 (g)", example = "0.2")
    private Float sugars;  
    
    @Schema(description = "나트륨 (mg)", example = "0.2")
    private Float sodium;

}
