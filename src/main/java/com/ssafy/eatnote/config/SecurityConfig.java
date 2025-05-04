package com.ssafy.eatnote.config;

import com.ssafy.eatnote.config.jwt.JwtAuthenticationFilter;
import com.ssafy.eatnote.config.jwt.JwtUtil;
import com.ssafy.eatnote.model.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
//    private final UserService userService;
    private final ApplicationContext applicationContext; // 🔄 UserService 대신 사용

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    	// 🔁 JwtAuthenticationFilter 내부에서 UserService Lazy하게 주입
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, applicationContext);

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 허용할 경로들
                .requestMatchers(
                		"/", 
                		"/api/auth/**", 
                		"/swagger-ui/**", 
                		"/v3/api-docs/**", 
                		"/css/**", 
                		"/js/**", 
                		"/images/**",
                		
                		
                		"/api/users/**"       // ← ✅ 회원가입 관련 API 모두 허용
        		).permitAll()
                // 그 외는 인증 필요
                .anyRequest().authenticated()
            )
            // 우리가 만든 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    
}
