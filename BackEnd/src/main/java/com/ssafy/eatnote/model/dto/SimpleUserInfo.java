package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
@Schema(description = "간단한 사용자 정보 DTO")
public class SimpleUserInfo {

    @Schema(description = "유저 ID", example = "1")
    private Long userId;

    @Schema(description = "닉네임", example = "햄짱")
    private String nickname;

    @Schema(description = "유저 유형 (1: 트레이너, 2: 일반회원)", example = "2")
    private Integer userType;

    @Schema(description = "프로필 이미지", example = "/uploads/profile/햄님.png")
    private String profileImage;
}