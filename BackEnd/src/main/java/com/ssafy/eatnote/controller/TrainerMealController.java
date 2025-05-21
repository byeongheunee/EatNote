package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.response.AllergyResponse;
import com.ssafy.eatnote.model.dto.response.MemberDetailsResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.PendingFollowRequestResponse;
import com.ssafy.eatnote.model.dto.response.TrainerMealDetailResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.AllergyService;
import com.ssafy.eatnote.model.service.MealService;
import com.ssafy.eatnote.model.service.MemberService;
import com.ssafy.eatnote.model.service.TrainerMealService;
import com.ssafy.eatnote.model.service.TrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "트레이너 회원 식단 조회 API", description = "트레이너가 특정 회원에 대한 식단 조회 api")
@RestController
@RequestMapping("api/trainer")
@RequiredArgsConstructor
public class TrainerMealController {

    private final TrainerMealService trainerMealService;
    private final MemberService memberService;
    private final AllergyService allergyService;
    
    
    
    @GetMapping("/meals/{mealId}")
    @Operation(summary = "트레이너 식단 피드백 작성용 상세 조회", description = "식단 정보 + 회원 체성분 + 알레르기 종합 조회")
    public ResponseEntity<?> getTrainerMealContext(@PathVariable Long mealId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long trainerId = userDetails.getUser().getUserId();
        
        if (userDetails.getUser().getUserType() != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(MyApiResponse.failure("FORBIDDEN", "트레이너만 접근할 수 있는 기능입니다."));
        }
        
        if (trainerMealService.hasTrainerWrittenFeedback(mealId, trainerId)) {
        	return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(MyApiResponse.failure("FEEDBACK_ALREADY_EXISTS", "이미 피드백을 작성한 식단입니다."));
        }
        
        // 1. 식단 상세 정보 조회 (작성자 ID 포함되도록)
        TrainerMealDetailResponse mealDetail = trainerMealService.getMealDetailById(mealId);
        Long memberId = mealDetail.getUserId(); // DTO에 있어야 함
       
        
        // 2. 회원 체성분 + 목표 + 트레이너 닉네임
        MemberDetails memberDetails = memberService.getDetailsByUserId(memberId);
        String trainerNickname = userDetails.getUser().getNickname(); // 혹은 userService로 조회
        MemberDetailsResponse memberResponse = MemberDetailsResponse.from(memberDetails, trainerNickname);

        // 3. 회원 알레르기
        List<AllergyResponse> allergies = allergyService.getAllergiesByUserId(memberId);

        // 4. 응답 조합
        Map<String, Object> response = new HashMap<>();
        response.put("meal", mealDetail);
        response.put("memberDetails", memberResponse);
        response.put("allergies", allergies);

        return ResponseEntity.ok(MyApiResponse.success(response, "TRAINER_MEAL_CONTEXT_SUCCESS", "피드백 위한 트레이너 식단 상세 조회 성공"));
    }
    
}
