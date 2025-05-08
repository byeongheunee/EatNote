package com.ssafy.eatnote.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.security.UserDetailsImpl;
import com.ssafy.eatnote.model.service.TokenBlacklistService;
import com.ssafy.eatnote.model.service.UserService;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;

    // Lazy 로딩
    private UserService userService;
    private TokenBlacklistService tokenBlacklistService;
    
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
        	
        	
        	
        	/////////////기존
//            String userId = jwtUtil.getUserId(token); // 토큰 안에 담긴 userId 클레임을 꺼낸다.
//
//            // Lazy하게 한 번만 UserService 불러오기
//            if (userService == null) {
//                userService = applicationContext.getBean(UserService.class);
//            }
//            
//            // DB에서 사용자 조회 (권한 정보 포함)
//            User user = userService.getUserById(Long.parseLong(userId));
//            if (user != null) {
//            	UserDetailsImpl userDetails = new UserDetailsImpl(user);
//                // 인증 객체 생성
//                UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // SecurityContext에 인증 정보 설정
//                SecurityContextHolder.getContext().setAuthentication(authentication); // 이 시점부터 Spring Security는 “인증된 사용자”라고 인식
//            }
            //////////////////
            ///
            ///
        	// Lazy 로딩 - 필요한 시점에만 Bean 주입
            if (userService == null) {
                userService = applicationContext.getBean(UserService.class);
            }
            if (tokenBlacklistService == null) {
                tokenBlacklistService = applicationContext.getBean(TokenBlacklistService.class);
            }

            // 블랙리스트에 등록된 토큰인지 확인
            if (tokenBlacklistService.isBlacklisted(token)) {
                log.warn("⛔ 블랙리스트에 등록된 JWT입니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("로그아웃된 토큰입니다.");
                return; // 다음 필터로 넘기지 않음
            }

            String userId = jwtUtil.getUserId(token);
            User user = userService.getUserById(Long.parseLong(userId));
            if (user != null) {
                UserDetailsImpl userDetails = new UserDetailsImpl(user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
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
