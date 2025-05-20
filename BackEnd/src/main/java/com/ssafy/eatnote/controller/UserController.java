package com.ssafy.eatnote.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.eatnote.model.dto.request.CommonUserUpdateRequest;
import com.ssafy.eatnote.model.dto.request.MemberUpdateRequest;
import com.ssafy.eatnote.model.dto.request.PasswordCheckRequest;
import com.ssafy.eatnote.model.dto.request.TrainerUpdateRequest;
import com.ssafy.eatnote.model.dto.request.UserRegisterRequest;
import com.ssafy.eatnote.model.dto.request.VerifyCodeRequest;
import com.ssafy.eatnote.model.dto.response.DailyNutritionStatsResponse;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyFeedbackResponse;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.AuthCodeStorageService;
import com.ssafy.eatnote.model.service.ChatGPTService;
import com.ssafy.eatnote.model.service.EmailService;
import com.ssafy.eatnote.model.service.MealService;
import com.ssafy.eatnote.model.service.MemberService;
import com.ssafy.eatnote.model.dto.response.WeeklyNutritionStatsResponse;
import com.ssafy.eatnote.model.service.AuthCodeStorageService;
import com.ssafy.eatnote.model.service.EmailService;
import com.ssafy.eatnote.model.service.TrainerMealService;
import com.ssafy.eatnote.model.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "회원 API", description = "회원가입, 이메일 인증, 닉네임 중복 확인 등의 API")
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MemberService memberService;
    private final EmailService emailService;
    private final MealService mealService;
    private final AuthCodeStorageService authCodeStorageService;
    private final TrainerMealService trainerMealService;
    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일이 이미 가입되어 있는지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용 가능한 이메일", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "409", description = "이미 등록된 이메일", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<MyApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean duplicated = userService.isEmailDuplicated(email);

        if (duplicated) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }
        return ResponseEntity.ok(MyApiResponse.success(false, "USER_EMAIL_AVAILABLE", "사용 가능한 이메일입니다."));
    }
    
    @PostMapping("/send-code")
    @Operation(summary = "이메일 인증 코드 발송", description = "입력한 이메일로 인증 코드를 전송합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<MyApiResponse<Void>> sendAuthCode(@RequestParam String email) {
        String authCode = generateRandomCode();
        emailService.sendAuthCode(email, authCode);
        authCodeStorageService.saveCode(email, authCode);
        return ResponseEntity.ok(MyApiResponse.success(null, "EMAIL_CODE_SENT", "인증 코드가 이메일로 전송되었습니다."));
    }

    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    @PostMapping("/verify-code")
    @Operation(summary = "이메일 인증 코드 검증", description = "사용자가 입력한 인증 코드가 유효한지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인증 성공", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "이메일 또는 코드 누락", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "409", description = "인증 실패 (만료 또는 불일치)", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<MyApiResponse<Void>> verifyAuthCode(@RequestBody VerifyCodeRequest request) {
    	log.info("📩 요청 받은 이메일: {}", request.getEmail());
    	log.info("📩 입력한 코드: {}", request.getCode());
    	
    	if (request.getEmail() == null || request.getCode() == null) {
    	    throw new IllegalArgumentException("이메일 또는 인증 코드가 누락되었습니다.");
    	}
    	
        boolean result = authCodeStorageService.verifyCode(request.getEmail(), request.getCode());

        if (!result) {
            throw new IllegalStateException("인증 코드가 올바르지 않거나 만료되었습니다.");
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "EMAIL_CODE_VERIFIED", "인증 코드가 확인되었습니다."));
    }
    
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 사용 가능한지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용 가능", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "닉네임 누락", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "409", description = "닉네임 중복", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<MyApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
    	if (nickname == null || nickname.isBlank()) {
    	    throw new IllegalArgumentException("닉네임을 입력해주세요.");
    	}

        boolean available = userService.isNicknameAvailable(nickname);

        if (available) {
            return ResponseEntity.ok(MyApiResponse.success(true, "NICKNAME_AVAILABLE", "사용 가능한 닉네임입니다."));
        } else {
            return ResponseEntity.badRequest()
                .body(MyApiResponse.failure("NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."));
        }
    }
    
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입", description = "회원 정보를 등록합니다. 트레이너는 자격증 이미지를 certFile로 함께 첨부해야 합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "회원 정보 JSON 파싱 실패", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "409", description = "회원가입 처리 실패", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<MyApiResponse<Void>> registerUser(
        @RequestPart("user") String userJson,
        @RequestPart(value = "file", required = false) MultipartFile profileImageFile,
        @RequestPart(value = "certFile", required = false) MultipartFile certFile  // 자격증 이미지 별도 추가
    ) {
        ObjectMapper mapper = new ObjectMapper();
        UserRegisterRequest request = null;

        try {
            request = mapper.readValue(userJson, UserRegisterRequest.class);
        } catch (IOException e) {
            log.error("userJson = {}", userJson);
            e.printStackTrace(); // 전체 에러 출력
            throw new IllegalArgumentException("회원 정보 형식이 잘못되었습니다.");
        }


        boolean success = userService.registerUser(request, profileImageFile, certFile);
        if (!success) {
            throw new IllegalStateException("회원가입 처리에 실패했습니다.");
        }

        return ResponseEntity.ok(MyApiResponse.success(null, "AUTH_REGISTER_SUCCESS", "회원가입 성공"));
    }
    
    @PostMapping("/check-password")
    @Operation(summary = "비밀번호 확인", description = "개인정보 수정 페이지 진입을 위한 비밀번호 확인")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비밀번호 일치"),
        @ApiResponse(responseCode = "401", description = "비밀번호 불일치 또는 인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<MyApiResponse<String>> checkPassword(
            @RequestBody PasswordCheckRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boolean matched = userService.checkPassword(userDetails.getUser().getUserId(), request.getPassword());

        if (matched) {
            return ResponseEntity.ok(
                MyApiResponse.success("비밀번호 확인 성공", "AUTH_PASSWORD_CHECK_SUCCESS", "비밀번호가 일치합니다.")
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                MyApiResponse.failure("AUTH_PASSWORD_CHECK_FAILED", "비밀번호가 일치하지 않습니다.")
            );
        }
    }
    
    @PutMapping("/member")
    @Operation(summary = "일반회원 정보 수정", description = "비밀번호 확인 이후 일반회원 정보를 수정합니다.")
    public ResponseEntity<MyApiResponse<Void>> updateMember(
            @RequestPart("common") String commonJson,
            @RequestPart("member") String memberJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getUserId();

        CommonUserUpdateRequest commonRequest;
        MemberUpdateRequest memberRequest;
        ObjectMapper mapper = new ObjectMapper();
        
        //
        System.out.println("commonJson = " + commonJson);
        System.out.println("memberJson = " + memberJson);
        //
        try {
        	commonRequest = mapper.readValue(commonJson, CommonUserUpdateRequest.class);
        	memberRequest = mapper.readValue(memberJson, MemberUpdateRequest.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }
        
        // 닉네임 중복 체크 (본인 닉네임은 예외)
        if (commonRequest.getNickname() != null &&
            !userService.isMyNicknameOrAvailable(userId, commonRequest.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(MyApiResponse.failure("NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."));
        }

        userService.updateMember(userId, commonRequest, memberRequest, file);

        return ResponseEntity.ok(
            MyApiResponse.success(null, "USER_UPDATE_SUCCESS", "회원 정보가 성공적으로 수정되었습니다.")
        );
    }
    
    @PutMapping("/trainer")
    @Operation(summary = "트레이너 정보 수정", description = "비밀번호 확인 이후 트레이너 정보를 수정합니다.")
    public ResponseEntity<MyApiResponse<Void>> updateTrainer(
            @RequestPart("common") String commonJson,
            @RequestPart("trainer") String trainerJson,
            @RequestPart(value = "file", required = false) MultipartFile profileImageFile,
            @RequestPart(value = "certFile", required = false) MultipartFile certFile,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getUserId();

        CommonUserUpdateRequest commonRequest;
        TrainerUpdateRequest trainerRequest;
        ObjectMapper mapper = new ObjectMapper();
        
        try {
        	commonRequest = mapper.readValue(commonJson, CommonUserUpdateRequest.class);
        	trainerRequest = mapper.readValue(trainerJson, TrainerUpdateRequest.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }
        
        if (commonRequest.getNickname() != null &&
            !userService.isMyNicknameOrAvailable(userId, commonRequest.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(MyApiResponse.failure("NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."));
        }

        userService.updateTrainer(userId, commonRequest, trainerRequest, profileImageFile, certFile);

        return ResponseEntity.ok(
            MyApiResponse.success(null, "USER_UPDATE_SUCCESS", "트레이너 정보가 성공적으로 수정되었습니다.")
        );
    }
    
    @GetMapping("/me/goal")
    @Operation(summary = "회원 목표 조회", description = "로그인한 일반 회원의 목표(goal)를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 목표 조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<MyApiResponse<String>> getMyGoal(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    	Long userId = userDetails.getUser().getUserId();
        String goal = memberService.getGoalByUserId(userId);
        return ResponseEntity.ok(MyApiResponse.success(goal, "USER_GOAL_FETCH_SUCCESS", "회원 목표 정보 조회에 성공했습니다."));
    }
    
    @Operation(summary = "주별 통계 조회 (본인)", description = "로그인한 사용자의 주별 평균 영양 통계를 조회합니다.")
    @GetMapping("/statistics/weekly")
    public MyApiResponse<List<WeeklyNutritionStatsResponse>> getMyStatistics(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        List<WeeklyNutritionStatsResponse> stats = trainerMealService.getWeeklyStats(userId);
        return MyApiResponse.success(stats, "USER_WEEKLY_STATS_SUCCESS", "주간 영양 통계 조회 성공");
    }
    
    @Operation(summary = "일별 통계 조회 (본인)", description = "로그인한 사용자의 일별 평균 영양 통계를 조회합니다.")
    @GetMapping("/statistics/daily")
    public MyApiResponse<List<DailyNutritionStatsResponse>> getMyDailyStatistics(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId();
        List<DailyNutritionStatsResponse> stats = trainerMealService.getDailyStats(userId);
        return MyApiResponse.success(stats, "USER_DAILY_STATS_SUCCESS", "일별 영양 통계 조회 성공");
    }
    
    @Operation(summary = "주간 AI 피드백 조회", description = "사용자의 주간 식사 통계 기반 GPT AI 피드백을 제공합니다.")
    @GetMapping("/statistics/weekly/ai-feedback")
    public MyApiResponse<WeeklyFeedbackResponse> getWeeklyAiFeedback(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        Long userId = userDetails.getUser().getUserId();
        WeeklyFeedbackResponse feedback = trainerMealService.getWeeklyGptFeedback(userId);
        return MyApiResponse.success(feedback, "USER_WEEKLY_AI_FEEDBACK_SUCCESS", "주간 AI 피드백 조회 성공");
    }

    @Operation(summary = "회원의 식단 목록 조회", description = "모든 회원의 userId를 이용해 해당 회원의 식단 목록을 조회합니다. 트레이너는 본인이 피드백한 식단인지 여부도 함께 확인 가능합니다.")
    @GetMapping("/user/{userId}/meals")
    public MyApiResponse<List<MealListViewResponse>> getUserMeals(@PathVariable Long userId,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long requesterId = userDetails.getUser().getUserId();
        List<MealListViewResponse> meals = mealService.getMealsForViewer(userId, requesterId);
        return MyApiResponse.success(meals, "USER_MEALS_FETCH_SUCCESS", "회원의 식단 목록 조회 성공");
    }
    
    @Operation(
	    summary = "회원 탈퇴",
	    description = "현재 로그인한 사용자의 상태를 'DELETED'로 변경하여 소프트 삭제 방식으로 탈퇴 처리합니다. 탈퇴 이후 해당 계정으로는 로그인할 수 없습니다.",
	    responses = {
	        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
	        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
	        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
	    }
	)
    @DeleteMapping("/me")
    public ResponseEntity<MyApiResponse<String>> deleteMyAccount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.markUserAsDeleted(userDetails.getUserId());
        return ResponseEntity.ok(MyApiResponse.success(null, "USER_DELETE_SUCCESS", "회원 탈퇴가 완료되었습니다."));
    }
    
}