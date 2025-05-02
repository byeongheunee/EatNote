package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원 상태 코드 DTO")
public class UserStatus {

    @Schema(description = "상태 코드", example = "ACTIVE")
    private String statusCode;

    @Schema(description = "상태 설명", example = "정상")
    private String description;
}
