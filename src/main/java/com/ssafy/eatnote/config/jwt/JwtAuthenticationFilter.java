package com.ssafy.eatnote.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.UserService;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;

    // Lazy 로딩
    private UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ApplicationContext applicationContext) {
        this.jwtUtil = jwtUtil;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        String token = extractToken(request); // Authorization: Bearer xxx 형식의 헤더에서 "xxx"만 잘라낸다.

        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) { // 토큰이 비어있지 않고, 서명 + 만료 시간 등 유효성 검사를 통과했는지 확인
            String userId = jwtUtil.getUserId(token); // 토큰 안에 담긴 userId 클레임을 꺼낸다.

            // Lazy하게 한 번만 UserService 불러오기
            if (userService == null) {
                userService = applicationContext.getBean(UserService.class);
            }
            
            // DB에서 사용자 조회 (권한 정보 포함)
            User user = userService.getUserById(Long.parseLong(userId));
            if (user != null) {
            	UserDetailsImpl userDetails = new UserDetailsImpl(user);
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication); // 이 시점부터 Spring Security는 “인증된 사용자”라고 인식
            }
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 이후의 토큰만 추출
        }
        return null;
    }
}
