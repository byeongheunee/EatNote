package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "최근 피드백 식단 응답 DTO")
public class MealFeedbackListViewResponse {

    @Schema(description = "식단 ID", example = "1001")
    private Long mealId;

    @Schema(description = "AI 분석 음식 목록", example = "닭가슴살, 고구마")
    private String detectedFoods;

    @Schema(description = "식사 타입 (breakfast/ lunch/ dinner/ extra)", example = "lunch")
    private String mealType;

    @Schema(description = "피드백 ID", example = "2001")
    private Long feedbackId;

    @Schema(description = "트레이너 사용자 ID", example = "3")
    private Long trainerId;

    @Schema(description = "피드백 내용", example = "단백질 비율이 적절하네요. 다음엔 채소도 함께 드셔보세요.")
    private String content;

    @Schema(description = "트레이너 점수 (10점 만점)", example = "8.0")
    private float trainerScore;

    @Schema(description = "회원 닉네임", example = "홍길동")
    private String userNickname;

    @Schema(description = "트레이너 닉네임", example = "트레이너김")
    private String trainerNickname;
    
    @Schema(description = "프로필 이미지 URL", example = "/images/profile.png")
    private String profileImage;

    @Schema(description = "소속 헬스장 이름", example = "핏밥짐")
    private String gymName;
    
}
