package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "게시판 생성 요청 DTO")
public class BoardCreateRequest {

    @Schema(description = "게시판 이름", example = "식단 공유 게시판")
    private String name;

    @Schema(description = "게시판 설명", example = "자신의 식단을 공유하고 피드백을 받는 공간입니다.")
    private String description;

    @Schema(description = "접근 권한 코드", example = "ALL")
    private String accessCode;
}
