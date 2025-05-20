package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "팔로우 상태 코드 DTO")
public class FollowStatus {

    @Schema(description = "팔로우 상태 코드", example = "PENDING")
    private String statusCode;

    @Schema(description = "설명", example = "요청중")
    private String description;
}
