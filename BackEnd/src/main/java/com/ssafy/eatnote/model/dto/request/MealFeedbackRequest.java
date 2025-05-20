package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식단 피드백 요청 DTO")
public class MealFeedbackRequest {

    @Schema(description = "식단 ID", example = "1001")
    private Long mealId;

    @Schema(description = "피드백 내용", example = "단백질 비율이 좋지만 채소가 부족해 보입니다.")
    private String content;

    @Schema(description = "트레이너 점수", example = "8.5")
    private float TrainerScore;
}
