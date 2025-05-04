package com.ssafy.eatnote.controller;

import com.ssafy.eatnote.config.jwt.JwtUtil;
import com.ssafy.eatnote.model.dto.MyApiResponse;
import com.ssafy.eatnote.model.dto.LoginRequest;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // 사용자 정보 조회
        User user = userService.getUserByEmail(email);

        // 사용자가 존재하지 않거나 비밀번호 불일치
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        	return ResponseEntity.status(401).body(MyApiResponse.failure("INVALID_CREDENTIALS", "이메일 또는 비밀번호가 틀렸습니다."));
        }

        // JWT 발급
        String token = jwtUtil.createToken(user.getUserId().toString());

        // 토큰 응답
        return ResponseEntity.ok(MyApiResponse.success(token, "USER_LOGIN_SUCCESS", "로그인에 성공했습니다."));
    }
}
