package com.ssafy.eatnote.model.security;

import com.ssafy.eatnote.model.dto.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 있다면 여기서 리턴 (예: ROLE_USER, ROLE_TRAINER 등)
        return Collections.emptyList(); // 기본은 빈 권한
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Spring Security 내부에서 필요
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 로그인 ID로 사용할 값 (보통 email)
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 → true면 사용 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성화 여부
    }
}
