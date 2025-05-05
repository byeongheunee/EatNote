package com.ssafy.eatnote.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

import com.ssafy.eatnote.model.dto.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 기본 응답 DTO")
public class UserResponse {

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "gildong123")
    private String nickname;

    @Schema(description = "성별", example = "M")
    private String gender;

    @Schema(description = "프로필 이미지 URL", example = "/images/profile.png")
    private String profileImage;

    @Schema(description = "가입일", example = "2025-05-02T15:30:00")
    private LocalDateTime createdAt;

    // 정적 팩토리 메서드 (Entity → Response 변환용)
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
