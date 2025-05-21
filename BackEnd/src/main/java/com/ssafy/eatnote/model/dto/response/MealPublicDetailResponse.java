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
@Schema(description = "공개 식단 상세 조회 응답 DTO")
public class MealPublicDetailResponse {

    @Schema(description = "식단 ID", example = "101")
    private Long mealId;
    
    @Schema(description = "식단 작성자 사용자 ID", example = "9")
    private Long userId;
    
    @Schema(description = "사용자 닉네임", example ="이벼이")
    private String userNickname;

    @Schema(description = "식사 종류", example = "점심")
    private String mealType;

    @Schema(description = "식사 시각", example = "2025-05-13T20:47:47")
    private LocalDateTime mealTime;

    @Schema(description = "AI 분석 음식 목록", example = "닭가슴살, 고구마")
    private String detectedFoods;

    @Schema(description = "총 칼로리 (kcal)", example = "350.0")
    private Float totalCalories;

    @Schema(description = "탄수화물 (g)", example = "40.2")
    private Float carbohydrates;

    @Schema(description = "단백질 (g)", example = "30.0")
    private Float protein;

    @Schema(description = "지방 (g)", example = "5.2")
    private Float fat;

    @Schema(description = "당류 (g)", example = "3.5")
    private Float sugars;

    @Schema(description = "나트륨 (mg)", example = "420.0")
    private Float sodium;

    @Schema(description = "이미지 URL", example = "/uploads/meals/9/2025-05-13_9_lunch.jpg")
    private String imageUrl;

    @Schema(description = "좋아요 수", example = "2")
    private int likeCount;
    
    @Schema(description = "싫어요 수", example = "1")
    private int dislikeCount;

    @Schema(description = "내 반응 상태 (LIKE / DISLIKE / null)", example = "DISLIKE")
    private String myReaction;
    
    @Schema(description = "AI 자동 분석 점수 (0.0 ~ 10.0)", example = "8.2")
    private Float autoScore;
    
    @Schema(description = "chatGPT생성 피드백", example = "나트륨과 지방이 다소 높은 편입니다. 채소나 과일을 함께 섭취하는 것을 권장합니다.")
    private String aiFeedback;
    
    @Schema(description = "트레이너 피드백 내용")
    private String trainerFeedback;

    @Schema(description = "트레이너 점수")
    private Float trainerScore;

    
    @Schema(description = "댓글 목록 (트리 구조)")
    private List<CommentResponse> comments;
} 
