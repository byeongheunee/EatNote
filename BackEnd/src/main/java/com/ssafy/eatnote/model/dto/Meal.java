package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식단 정보 DTO")
public class Meal {

    @Schema(description = "식단 ID", example = "1001")
    private Long mealId;

    @Schema(description = "작성자 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "식단 이미지 URL", example = "https://fitbob.s3.amazonaws.com/images/meal1.jpg")
    private String imageUrl;

    @Schema(description = "AI 분석 음식 목록", example = "닭가슴살, 고구마")
    private String detectedFoods;

    @Schema(description = "총 칼로리", example = "530.5")
    private Float totalCalories;

    @Schema(description = "탄수화물(g)", example = "60.0")
    private Float carbohydrates;

    @Schema(description = "단백질(g)", example = "45.0")
    private Float protein;

    @Schema(description = "지방(g)", example = "10.0")
    private Float fat;

    @Schema(description = "당류(g)", example = "8.0")
    private Float sugars;

    @Schema(description = "나트륨(mg)", example = "1200.5")
    private Float sodium;

    @Schema(description = "식사 타입 (breakfast/ lunch/ dinner/ extra)", example = "lunch")
    private String mealType;

    @Schema(description = "식사 시각", example = "2025-05-02T12:30:00")
    private LocalDateTime mealTime;

    @Schema(description = "작성 시간", example = "2025-05-02T12:35:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "자동 점수 (0~10점)", example = "8.5")
    private Float autoScore;
    
    @Schema(description = "chatGPT 자동 생성 피드", example = "나트륨과 지방이 다소 높은 편입니다. 채소나 과일을 함께 섭취하는 것을 권장합니다.")
    private String aiFeedback;
    
    @Schema(description = "좋아요 수", example = "5")
    private Integer likeCount;

    @Schema(description = "싫어요 수", example = "2")
    private Integer dislikeCount;

    @Schema(description = "내 반응 상태 (LIKE / DISLIKE / null)", example = "LIKE")
    private String myReaction;
    
}
