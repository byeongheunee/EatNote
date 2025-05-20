package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시판 수정 요청 DTO")
public class BoardUpdateRequest {

    @Schema(description = "게시판 이름", example = "식단 공유 게시판")
    private String name;

    @Schema(description = "게시판 설명", example = "수정된 설명")
    private String description;

    @Schema(description = "접근 권한 코드", example = "TRAINER_ONLY")
    private String accessCode;
}
