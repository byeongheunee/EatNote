package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Schema(description = "트레이너 전용 정보 수정 요청 DTO")
public class TrainerUpdateRequest {

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "소속 헬스장", example = "핏밥짐")
    private String gymName;

    @Schema(description = "헬스장 홈페이지", example = "https://fitbobgym.com")
    private String gymWebsite;

    @Schema(description = "자격증 번호", example = "CERT-2024-0001")
    private String certificationNumber;

    @Schema(description = "사업자 등록번호", example = "123-45-67890")
    private String businessNumber;

    @Schema(description = "트레이너 자기소개", example = "10년 경력의 퍼스널 트레이너입니다.")
    private String introduction;

    @Schema(description = "경력 요약", example = "2015~2020 스포짐, 2021~현재 핏밥짐")
    private String career;

    @Schema(description = "인스타그램 URL", example = "https://instagram.com/fitbob_trainer")
    private String instagramUrl;
}
