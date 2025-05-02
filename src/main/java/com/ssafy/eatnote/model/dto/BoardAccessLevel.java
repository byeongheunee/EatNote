package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시판 접근 권한 코드 DTO")
public class BoardAccessLevel {

    @Schema(description = "접근 권한 코드", example = "TRAINER_ONLY")
    private String accessCode;

    @Schema(description = "설명", example = "트레이너 전용 게시판")
    private String description;
}
