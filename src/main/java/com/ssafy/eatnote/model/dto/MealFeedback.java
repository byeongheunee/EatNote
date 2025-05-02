package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식단 피드백 DTO")
public class MealFeedback {

    @Schema(description = "피드백 ID", example = "2001")
    private Long feedbackId;

    @Schema(description = "식단 ID", example = "1001")
    private Long mealId;

    @Schema(description = "트레이너 사용자 ID", example = "3")
    private Long trainerId;

    @Schema(description = "피드백 내용", example = "단백질 비율이 적절하네요. 다음엔 채소도 함께 드셔보세요.")
    private String content;

    @Schema(description = "작성 시각", example = "2025-05-02T14:10:00")
    private LocalDateTime createdAt;
}
