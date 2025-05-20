package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "트레이너용 식단 상세 조회 응답 DTO")
public class TrainerMealDetailResponse {

    @Schema(description = "식단 ID", example = "1001")
    private Long mealId;

    @Schema(description = "식사 종류", example = "점심")
    private String mealType;

    @Schema(description = "식사 시각", example = "2025-05-11T13:10:00")
    private LocalDateTime mealTime;

    @Schema(description = "AI 분석 음식 목록", example = "닭가슴살, 샐러드")
    private String detectedFoods;

    @Schema(description = "자동 영양 점수", example = "8.5")
    private Float autoScore;

    @Schema(description = "총 칼로리", example = "732.5")
    private Float totalCalories;

    @Schema(description = "탄수화물 (g)", example = "90.0")
    private Float carbohydrates;

    @Schema(description = "단백질 (g)", example = "30.0")
    private Float protein;

    @Schema(description = "지방 (g)", example = "20.0")
    private Float fat;

    @Schema(description = "당류 (g)", example = "5.0")
    private Float sugars;

    @Schema(description = "나트륨 (mg)", example = "1800.0")
    private Float sodium;

    @Schema(description = "회원 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "회원 키 (cm)", example = "175.5")
    private Float height;

    @Schema(description = "회원 몸무게 (kg)", example = "68.2")
    private Float weight;

    @Schema(description = "회원 목표", example = "감량")
    private String goal;

    @Schema(description = "성별", example = "M")
    private String gender;

    @Schema(description = "나이", example = "30")
    private Integer age;
    
    @Schema(description = "이미지경로", example = "/uploads/meals/9/2025-05-13_9_lunch.jpg")
    private String imageUrl;
    
    @Schema(description = "해당 식단을 등록한 회원의 userId", example = "42")
    private Long userId;
    
    @Schema(description = "GPT AI 피드백")
    private String aiFeedback;
}
