package com.ssafy.eatnote.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "트레이너 상세 정보 요청 DTO")
public class TrainerDetailsRequest {

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "헬스장 이름", example = "핏밥짐")
    private String gymName;

    @Schema(description = "헬스장 홈페이지", example = "https://fitbobgym.com")
    private String gymWebsite;

    @Schema(description = "자격증 번호", example = "CERT-2024-0001")
    private String certificationNumber;

    @Schema(description = "사업자 등록 번호", example = "123-45-67890")
    private String businessNumber;

    @Schema(description = "자기소개", example = "건강을 디자인하는 트레이너입니다.")
    private String introduction;

    @Schema(description = "경력 요약", example = "2020~2024 핏밥짐 강남")
    private String career;

    @Schema(description = "인스타그램 URL", example = "https://instagram.com/fitbob_trainer")
    private String instagramUrl;

}
