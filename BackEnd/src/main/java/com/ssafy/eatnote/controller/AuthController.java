package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.config.jwt.JwtUtil;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.SimpleUserInfo;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.request.LoginRequest;
import com.ssafy.eatnote.model.dto.response.AllergyResponse;
import com.ssafy.eatnote.model.dto.response.LoginResponse;
import com.ssafy.eatnote.model.dto.response.MemberDetailsResponse;
import com.ssafy.eatnote.model.dto.response.MyApiResponse;
import com.ssafy.eatnote.model.dto.response.TrainerDetailsResponse;
import com.ssafy.eatnote.model.dto.response.UserDetailResponse;
import com.ssafy.eatnote.model.service.AllergyService;
import com.ssafy.eatnote.model.service.MemberService;
import com.ssafy.eatnote.model.service.TokenBlacklistService;
import com.ssafy.eatnote.model.service.TrainerService;
import com.ssafy.eatnote.model.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "로그인 및 사용자 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final TrainerService trainerService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AllergyService allergyService;
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 사용하여 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 틀림", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        System.out.println("email : " + email);
        System.out.println("password : " + password);
        // 사용자 정보 조회
        User user = userService.getUserByEmail(email);

        // 사용자가 존재하지 않거나 비밀번호 불일치
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        	return ResponseEntity.status(401).body(MyApiResponse.failure("INVALID_CREDENTIALS", "이메일 또는 비밀번호가 틀렸습니다."));
        }

        // 회원 상태 확인
        String status = user.getStatus();
        if ("DELETED".equals(status)) {
            return ResponseEntity.status(401).body(
                MyApiResponse.failure("USER_DELETED", "탈퇴한 계정입니다. 다시 가입해주세요.")
            );
        }
        if ("INACTIVE".equals(status)) {
            return ResponseEntity.status(401).body(
                MyApiResponse.failure("USER_INACTIVE", "일시 정지된 계정입니다. 관리자에게 문의해주세요.")
            );
        }
        
        // JWT 발급
        String token = jwtUtil.createToken(user.getUserId().toString());

        // 응답 구성
        SimpleUserInfo userInfo = SimpleUserInfo.builder()
            .userId(user.getUserId())
            .nickname(user.getNickname())
            .userType(user.getUserType())
            .profileImage(user.getProfileImage())
            .build();

        LoginResponse response = LoginResponse.builder()
            .accessToken(token)
            .user(userInfo)
            .build();

        return ResponseEntity.ok(MyApiResponse.success(response, "USER_LOGIN_SUCCESS", "로그인에 성공했습니다."));
    }

    
    @PostMapping("/logout")
    @Operation(summary = "로그아웃 (JWT 블랙리스트 등록)", description = "Authorization 헤더에서 JWT를 추출하여 블랙리스트에 등록함으로써 로그아웃 처리합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "400", description = "Authorization 헤더가 없거나 유효하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = jwtUtil.resolveToken(authorizationHeader);  // "Bearer " 제거

        // 토큰 만료까지 남은 시간 계산 (초 단위)
        long expiration = jwtUtil.getRemainingTime(token);

        // 블랙리스트에 등록
        tokenBlacklistService.blacklistToken(token, expiration);

        return ResponseEntity.ok("로그아웃 완료!");
    }
    
    
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "JWT 토큰을 통해 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "JWT 토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "해당 사용자 없음", content = @Content(schema = @Schema(implementation = MyApiResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(schema = @Schema(implementation = MyApiResponse.class)))
    })
    public ResponseEntity<?> getMyInfo(HttpServletRequest request) {
        // 1. 토큰 추출 및 유효성 검사
        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401)
                    .body(MyApiResponse.failure("INVALID_TOKEN", "유효하지 않은 토큰입니다."));
        }

        // 2. userId 추출 및 사용자 조회
        Long userId = Long.parseLong(jwtUtil.getUserId(token));
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(404)
                    .body(MyApiResponse.failure("USER_NOT_FOUND", "사용자를 찾을 수 없습니다."));
        }

        // 3. 공통 User 정보 변환
        UserDetailResponse userResponse = UserDetailResponse.from(user);

        // 4. 상세 정보 분기
        Map<String, Object> response = new HashMap<>();
        response.put("user", userResponse);

        if (user.getUserType() == 1) { // 트레이너
        	TrainerDetails trainerDetails = trainerService.getDetailsByUserId(userId);
            if (trainerDetails != null) {
                response.put("trainerDetails", TrainerDetailsResponse.from(trainerDetails));
            }
        } else if (user.getUserType() == 2) { // 일반 회원
            MemberDetails memberDetails = memberService.getDetailsByUserId(userId);
            if (memberDetails != null) {
                String trainerNickname = null;

                if (memberDetails.getTrainerId() != null) {
                    User trainer = userService.getUserById(memberDetails.getTrainerId());
                    if (trainer != null) {
                        trainerNickname = trainer.getNickname();
                    }
                }

                response.put("memberDetails", MemberDetailsResponse.from(memberDetails, trainerNickname));
                
                // 알레르기 정보 추가
                List<AllergyResponse> allergies = allergyService.getAllergiesByUserId(userId);
                response.put("allergies", allergies);
            }
        }

        return ResponseEntity.ok(MyApiResponse.success(response, "GET_MY_INFO_SUCCESS", "개인정보 조회 성공"));
    }
}
