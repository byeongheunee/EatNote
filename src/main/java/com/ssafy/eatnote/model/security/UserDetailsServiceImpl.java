package com.ssafy.eatnote.model.security;

import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao userDao;

    /**
     * JWT 토큰에서 추출한 userId(String)를 기반으로 사용자 정보를 조회해 인증 객체 생성
     */
    @Override
    public UserDetails loadUserByUsername(String userIdAsString) throws UsernameNotFoundException {
        try {
            Long userId = Long.parseLong(userIdAsString);

            User user = userDao.selectUserById(userId);
            if (user == null) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId);
            }

            return new UserDetailsImpl(user);
            
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("잘못된 사용자 ID 형식입니다: " + userIdAsString);
        }
    }
}
