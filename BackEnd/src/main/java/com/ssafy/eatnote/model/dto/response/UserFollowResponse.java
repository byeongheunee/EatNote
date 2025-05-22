package com.ssafy.eatnote.model.dto.response;

import com.ssafy.eatnote.model.dto.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팔로우 목록 사용자 응답 DTO")
public class UserFollowResponse {

	@Schema(description = "회원 ID", example = "1")
    private Long userId;
	
    @Schema(description = "닉네임", example = "fitgirl99")
    private String nickname;

    @Schema(description = "성별", example = "F")
    private String gender;

    @Schema(description = "프로필 이미지 URL", example = "/images/profile1.jpg")
    private String profileImage;
    
    @Schema(description = "유저 ID", example = "7")
    private Long userId;

    @Schema(description = "유저 타입 (0: 일반회원, 1: 트레이너)", example = "0")
    private int userType;
    
    public static UserFollowResponse from(User user) {
        return UserFollowResponse.builder()
        		.userId(user.getUserId())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .userType(user.getUserType())
                .build();
    }
}
