package com.ssafy.eatnote.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원 상세 정보 DTO")
public class MemberDetails {

    @Schema(description = "회원 ID", example = "1")
    private Long userId;

    @Schema(description = "키(cm)", example = "175.5")
    private Float height;

    @Schema(description = "몸무게(kg)", example = "70.0")
    private Float weight;

    @Schema(description = "체수분", example = "50.3")
    private Float bodyWater;

    @Schema(description = "단백질량", example = "14.2")
    private Float protein;

    @Schema(description = "무기질", example = "3.5")
    private Float mineral;

    @Schema(description = "체지방", example = "18.0")
    private Float bodyFat;

    @Schema(description = "골격근량", example = "28.7")
    private Float skeletalMuscle;

    @Schema(description = "체지방량", example = "13.2")
    private Float bodyFatMass;

    @Schema(description = "BMI 지수", example = "22.5")
    private Float bmi;

    @Schema(description = "체지방률", example = "18.7")
    private Float bodyFatPercentage;

    @Schema(description = "목표 (감량, 증량 등)", example = "감량")
    private String goal;

    @Schema(description = "연결된 트레이너 ID", example = "3")
    private Long trainerId;
    
    @Schema(description = "선택한 알레르기 ID 목록", example = "[1, 13, 21]")
    private List<Integer> allergyIds;
}
