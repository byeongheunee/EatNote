package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팔로우 요청 응답 DTO")
public class FollowRespondRequest {

    @Schema(description = "팔로우 요청 ID", example = "123")
    private Long followId;

    @Schema(description = "응답 타입 (ACCEPT 또는 REJECT)", example = "ACCEPT")
    private String action;
}
