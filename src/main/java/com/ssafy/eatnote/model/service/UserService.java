package com.ssafy.eatnote.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.UserRegisterRequest;

public interface UserService {
	
	/**
     * 회원가입 처리 - 유저 기본 정보 및 상세 정보 저장
     *
     * @param request 사용자 정보 + 상세 정보
     * @param file 프로필 이미지 파일 (nullable)
     * @return 성공 여부
     */
    boolean registerUser(UserRegisterRequest request, MultipartFile file);

//    // 로그인 (id, password로 사용자 조회)
//    User login(String userId, String userPwd) throws Exception;
    
	// id로 사용자 정보 가져오기
    User getUserById(Long userId);
    
    // email로 사용자 정보 가져오기
    User getUserByEmail(String email);
    
    // 전체 사용자 목록 조회
    List<User> getAllUsers();
    
    // 회원정보 수정
    boolean updateUser(User user);

    // 회원 탈퇴
    boolean deleteUser(Long userId);
    
    // email 확인
    boolean isEmailDuplicated(String email);
    
    // 닉네임 중복 체크
    boolean isNicknameAvailable(String nickname);
	
}