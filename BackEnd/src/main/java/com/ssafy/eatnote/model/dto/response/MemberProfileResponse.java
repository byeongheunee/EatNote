package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class MemberProfileResponse {

	@Schema(description = "회원 ID", example = "21")
    private Long userId;

    @Schema(description = "유저 타입 (2: 일반회원)", example = "2")
    private int userType;
    
    @Schema(description = "닉네임", example = "gildong123")
    private String nickname;

    @Schema(description = "성별 (gender_type 테이블 참조)", example = "M")
    private String gender;

    @Schema(description = "프로필 이미지 URL", example = "/images/profile.png")
    private String profileImage;

    @Schema(description = "목표 (감량, 증량 등)", example = "감량")
    private String goal;

    @Schema(description = "선택한 알레르기 ID 목록", example = "[1, 13, 21]")
    private List<Integer> allergyIds;

    @Schema(description = "총 식단 기록 수", example = "32")
    private int totalMeals;

    @Schema(description = "평균 식단 점수", example = "82.5")
    private Double averageScore;

    @Schema(description = "연속 식단 기록일", example = "7")
    private int consecutiveDays;

    @Schema(description = "팔로잉 수", example = "15")
    private int followingCount;
    
    @Schema(description = "팔로우 상태", example = "PENDING") // NONE, PENDING, ACCEPTED, REJECTED
    private String followStatus;
}
