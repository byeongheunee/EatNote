package com.ssafy.eatnote.controller;

import java.io.IOException;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.eatnote.model.dto.MyApiResponse;
import com.ssafy.eatnote.model.dto.UserRegisterRequest;
import com.ssafy.eatnote.model.dto.VerifyCodeRequest;
import com.ssafy.eatnote.model.service.AuthCodeStorageService;
import com.ssafy.eatnote.model.service.EmailService;
import com.ssafy.eatnote.model.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final AuthCodeStorageService authCodeStorageService;

    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일이 이미 가입되어 있는지 확인합니다.")
    public ResponseEntity<MyApiResponse<Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean duplicated = userService.isEmailDuplicated(email);

        if (duplicated) {
            return ResponseEntity.ok(
                MyApiResponse.success(true, "USER_EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다.")
            );
        } else {
            return ResponseEntity.ok(
                MyApiResponse.success(false, "USER_EMAIL_AVAILABLE", "사용 가능한 이메일입니다.")
            );
        }
    }
    
    @PostMapping("/send-code")
    @Operation(summary = "이메일 인증 코드 발송", description = "입력한 이메일로 인증 코드를 전송합니다.")
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
    public ResponseEntity<MyApiResponse<Void>> verifyAuthCode(@RequestBody VerifyCodeRequest request) {
    	log.info("📩 요청 받은 이메일: {}", request.getEmail());
    	log.info("📩 입력한 코드: {}", request.getCode());
    	
    	if (request.getEmail() == null || request.getCode() == null) {
            return ResponseEntity.badRequest().body(MyApiResponse.failure("EMAIL_CODE_INVALID", "이메일 또는 인증 코드가 누락되었습니다."));
        }
    	
        boolean result = authCodeStorageService.verifyCode(request.getEmail(), request.getCode());

        if (result) {
            return ResponseEntity.ok(MyApiResponse.success(null, "EMAIL_CODE_VERIFIED", "인증 코드가 확인되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(MyApiResponse.failure("EMAIL_CODE_INVALID", "인증 코드가 올바르지 않거나 만료되었습니다."));
        }
    }
    
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "닉네임이 사용 가능한지 확인합니다.")
    public ResponseEntity<MyApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return ResponseEntity.badRequest()
                .body(MyApiResponse.failure("NICKNAME_EMPTY", "닉네임을 입력해주세요."));
        }

        boolean available = userService.isNicknameAvailable(nickname);

        if (available) {
            return ResponseEntity.ok(MyApiResponse.success(true, "NICKNAME_AVAILABLE", "사용 가능한 닉네임입니다."));
        } else {
            return ResponseEntity.badRequest()
                .body(MyApiResponse.failure("NICKNAME_DUPLICATED", "이미 사용 중인 닉네임입니다."));
        }
    }
    
    @Operation(summary = "회원가입", description = "회원 정보를 등록합니다.")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse<String>> registerUser(
        @RequestPart("user") String userJson,
        @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserRegisterRequest request = mapper.readValue(userJson, UserRegisterRequest.class);

            boolean success = userService.registerUser(request, file);
            if (success) {
                return ResponseEntity.ok(
                    MyApiResponse.success(null, "AUTH_REGISTER_SUCCESS", "회원가입 성공")
                );
            } else {
                return ResponseEntity.badRequest().body(
                    MyApiResponse.failure("AUTH_REGISTER_FAILED", "회원가입 실패")
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MyApiResponse.failure("INTERNAL_SERVER_ERROR", "회원가입 중 오류 발생: " + e.getMessage()));
        }
    }
    
}