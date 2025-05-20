package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *  회원의 식단 목록을 조회할 때 사용하는 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "회원 식단 목록 응답 DTO (공개용)")
public class MealListViewResponse {

    @Schema(description = "식단 ID", example = "1001")
    private Long mealId;

    @Schema(description = "회원 ID", example = "5")
    private Long userId;

    @Schema(description = "회원 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "식사 종류", example = "점심")
    private String mealType;

    @Schema(description = "식사 시각", example = "2025-05-11T12:30:00")
    private LocalDateTime mealTime;

    @Schema(description = "자동 영양 점수", example = "8.5")
    private Float autoScore;

    @Schema(description = "트레이너 피드백 점수", example = "9.0")
    private Float trainerScore; // (평균)

    @Schema(description = "AI 분석 음식 이름 목록", example = "김치찌개, 계란말이")
    private String detectedFoods;

    @Schema(description = "총 섭취 열량", example = "720.5")
    private Float totalCalories;

    @Schema(description = "식단 이미지 URL", example = "/uploads/meals/5/2025-05-11_5_lunch.jpg")
    private String imageUrl;
    
    @Schema(description = "현재 사용자(트레이너)가 피드백한 식단 여부", example = "true")
    private Boolean isFeedbackedByMe; // (트레이너인 경우 true/false, 회원인경우 false)
    
    @Schema(description = "피드백 수", example = "2")
    private int feedbackCount;

    @Schema(description = "좋아요 수", example = "12")
    private int likeCount;
    
    @Schema(description = "싫어요 수", example = "1")
    private int dislikeCount;

    @Schema(description = "내 반응 상태 (LIKE / DISLIKE / null)", example = "LIKE")
    private String myReaction;

}
