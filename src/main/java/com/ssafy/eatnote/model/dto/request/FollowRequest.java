package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팔로우 요청 DTO - 상대방 닉네임 기반 요청")
public class FollowRequest {

    @Schema(description = "팔로우할 대상 유저의 닉네임", example = "trainer_kim")
    private String nickname;
}