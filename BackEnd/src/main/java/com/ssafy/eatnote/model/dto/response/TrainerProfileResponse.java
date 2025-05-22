package com.ssafy.eatnote.model.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TrainerProfileResponse {

    @Schema(description = "닉네임", example = "gildong123")
    private String nickname;

    @Schema(description = "성별 (gender_type 테이블 참조)", example = "M")
    private String gender;

    @Schema(description = "프로필 이미지 URL", example = "/images/profile.png")
    private String profileImage;

    @Schema(description = "트레이너 자기소개", example = "10년 경력의 퍼스널 트레이너입니다.")
    private String introduction;

    @Schema(description = "인스타그램 URL", example = "https://instagram.com/fitbob_trainer")
    private String instagramUrl;

    @Schema(description = "소속 헬스장 이름", example = "핏밥짐")
    private String gymName;

    @Schema(description = "헬스장 홈페이지 URL", example = "https://fitbobgym.com")
    private String gymWebsite;

    @Schema(description = "총 피드백 수", example = "123")
    private int totalFeedbacks;

    @Schema(description = "팔로잉 수", example = "45")
    private int followingCount;
    
    @Schema(description = "요청 사용자가 이 사용자를 팔로우 중인지 여부", example = "true")
    private boolean following;
}
