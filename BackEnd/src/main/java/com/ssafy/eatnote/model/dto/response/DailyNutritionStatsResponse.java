package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "일별 식사 통계 응답 DTO")
public class DailyNutritionStatsResponse {

    @Schema(description = "날짜 (YYYY-MM-DD)", example = "2025-05-15")
    private String day;

    @Schema(description = "평균 총 칼로리", example = "1420.4")
    private Float avgCalories;

    @Schema(description = "평균 탄수화물 (g)", example = "190.1")
    private Float avgCarbohydrates;

    @Schema(description = "평균 단백질 (g)", example = "85.5")
    private Float avgProtein;

    @Schema(description = "평균 지방 (g)", example = "62.3")
    private Float avgFat;

    @Schema(description = "평균 트레이너 피드백 점수 (0~10점)", example = "6.8")
    private Float trainerScore;

    @Schema(description = "평균 AI 피드백 점수 (0~10점)", example = "7.1")
    private Float autoScore;

    @Schema(description = "식사 횟수", example = "3")
    private Integer mealCount;
    
    @Schema(description = "총 칼로리", example = "1420.4")
    private Float totalCalories;

    @Schema(description = "총 탄수화물 (g)", example = "190.1")
    private Float totalCarbohydrates;

    @Schema(description = "총 단백질 (g)", example = "85.5")
    private Float totalProtein;

    @Schema(description = "총 지방 (g)", example = "62.3")
    private Float totalFat;

}
