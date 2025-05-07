package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 알레르기 등록 요청 DTO")
public class UserAllergyRequest {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "알레르기 ID 리스트", example = "[1, 2, 3]")
    private List<Integer> allergyIds;
}
