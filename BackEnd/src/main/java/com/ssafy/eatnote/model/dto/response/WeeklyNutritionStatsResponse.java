package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주별 식사 통계 응답 DTO")
public class WeeklyNutritionStatsResponse {

    @Schema(description = "주차 (연도-W주차)", example = "2025-W18")
    private String week;

    @Schema(description = "평균 총 칼로리", example = "1582.4")
    private Float avgCalories;

    @Schema(description = "평균 탄수화물 (g)", example = "202.5")
    private Float avgCarbohydrates;

    @Schema(description = "평균 단백질 (g)", example = "95.1")
    private Float avgProtein;

    @Schema(description = "평균 지방 (g)", example = "56.0")
    private Float avgFat;

    @Schema(description = "평균 트레이너 피드백 점수 (0~10점)", example = "7.3")
    private Float trainerScore;
    
    @Schema(description = "평균 AI 피드백 점수 (0~10점)", example = "7.3")
    private Float autoScore;

    @Schema(description = "식사 횟수", example = "12")
    private Integer mealCount;
    
    @Schema(description = "주간 총 칼로리", example = "11047.0")
    private Float totalCalories;

    @Schema(description = "주간 총 탄수화물 (g)", example = "1253.6")
    private Float totalCarbohydrates;

    @Schema(description = "주간 총 단백질 (g)", example = "668.2")
    private Float totalProtein;

    @Schema(description = "주간 총 지방 (g)", example = "392.4")
    private Float totalFat;

}
