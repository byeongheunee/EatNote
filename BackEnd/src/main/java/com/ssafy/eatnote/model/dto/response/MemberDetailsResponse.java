package com.ssafy.eatnote.model.dto.response;

import java.util.List;

import com.ssafy.eatnote.model.dto.MemberDetails;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원 상세 응답 DTO")
public class MemberDetailsResponse {

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

    @Schema(description = "최초 연결된 트레이너 닉네임", example = "trainer_hong")
    private String trainerNickname;
    
    
    public static MemberDetailsResponse from(MemberDetails details, String trainerNickname) {
        return MemberDetailsResponse.builder()
                .height(details.getHeight())
                .weight(details.getWeight())
                .bodyWater(details.getBodyWater())
                .protein(details.getProtein())
                .mineral(details.getMineral())
                .bodyFat(details.getBodyFat())
                .skeletalMuscle(details.getSkeletalMuscle())
                .bodyFatMass(details.getBodyFatMass())
                .bmi(details.getBmi())
                .bodyFatPercentage(details.getBodyFatPercentage())
                .goal(details.getGoal())
                .trainerNickname(trainerNickname)
                .build();
    }
}
