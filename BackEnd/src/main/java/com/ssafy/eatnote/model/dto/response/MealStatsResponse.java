package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자의 식단 통계 정보 DTO")
public class MealStatsResponse {

    @Schema(description = "연속 식단 기록일", example = "7")
    private int consecutiveDays;

    @Schema(description = "총 식단 기록 수", example = "32")
    private int totalMeals;

    @Schema(description = "피드백 받은 식단 수", example = "28")
    private int feedbackReceived;

    @Schema(description = "평균 섭취 칼로리", example = "1850")
    private int averageCalories;
}
