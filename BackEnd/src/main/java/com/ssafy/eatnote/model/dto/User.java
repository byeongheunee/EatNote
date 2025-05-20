package com.ssafy.eatnote.model.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User 정보 DTO")
public class User {

    @Schema(description = "회원 ID (자동 증가)", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "test@example.com")
    private String email;

    @Schema(description = "비밀번호 (암호화된 값)", example = "hashed_password")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "닉네임", example = "gildong123")
    private String nickname;
    
    @Schema(description = "나이", example = "25")
    private Integer age;

    @Schema(description = "성별 (gender_type 테이블 참조)", example = "M")
    private String gender;

    @Schema(description = "프로필 이미지 URL", example = "/images/profile.png")
    private String profileImage;

    @Schema(description = "회원 유형 (user_type 테이블 참조)", example = "2")
    private Integer userType;

    @Schema(description = "계정 상태 (user_status 테이블 참조)", example = "ACTIVE")
    private String status;

    @Schema(description = "가입일", example = "2025-05-02T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일", example = "2025-05-02T15:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "최근 로그인 시간", example = "2025-05-01T21:00:00")
    private LocalDateTime lastLogin;
}
