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

    @Schema(description = "식사 시각", example = "2025-05-02T12:30:00")
    private LocalDateTime mealTime;

    @Schema(description = "작성 시간", example = "2025-05-02T12:35:00")
    private LocalDateTime createdAt;
}
