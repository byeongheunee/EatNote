package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "GPT 기반 주간 식단 피드백 응답")
public class WeeklyFeedbackResponse {

    @Schema(description = "주의할 점", example = "지방과 나트륨 섭취가 다소 높습니다.")
    private String warning;

    @Schema(description = "개선 팁", example = "채소를 추가하고 가공식품 섭취를 줄이세요.")
    private String tip;

    @Schema(description = "추천 음식 리스트")
    private List<RecommendedFood> recommendedFoods;

    @Data
    public static class RecommendedFood {
        @Schema(description = "음식 이름", example = "두부 샐러드")
        private String name;

        @Schema(description = "추천 이유", example = "단백질이 풍부하고 칼로리가 낮습니다.")
        private String reason;
    }
}
