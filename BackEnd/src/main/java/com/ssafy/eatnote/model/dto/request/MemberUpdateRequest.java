package com.ssafy.eatnote.model.dto.request;

import java.util.List;

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
@Schema(description = "회원 전용 정보 수정 요청 DTO")
public class MemberUpdateRequest {

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

    @Schema(description = "선택한 알레르기 ID 목록", example = "[1, 13, 21]")
    private List<Integer> allergyIds;
}
