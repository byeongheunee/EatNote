package com.ssafy.eatnote.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "트레이너 상세 정보 DTO")
public class TrainerDetails {

    @Schema(description = "트레이너 사용자 ID", example = "5")
    private Long userId;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "소속 헬스장 이름", example = "핏밥짐")
    private String gymName;

    @Schema(description = "헬스장 홈페이지 URL", example = "https://fitbobgym.com")
    private String gymWebsite;

    @Schema(description = "자격증 번호", example = "CERT-2024-0001")
    private String certificationNumber;

    @Schema(description = "자격증 이미지 경로", example = "/uploads/certifications/cert1.png")
    private String certificationImage;

    @Schema(description = "사업자 등록 번호", example = "123-45-67890")
    private String businessNumber;

    @Schema(description = "트레이너 자기소개", example = "10년 경력의 퍼스널 트레이너입니다.")
    private String introduction;

    @Schema(description = "경력 요약", example = "2015~2020 스포짐 트레이너, 2021~현재 핏밥짐 대표 트레이너")
    private String career;

    @Schema(description = "인스타그램 URL", example = "https://instagram.com/fitbob_trainer")
    private String instagramUrl;

    @Schema(description = "인증 여부", example = "true")
    private Boolean isVerified;
}
