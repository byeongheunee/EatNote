package com.ssafy.eatnote.model.service;

import java.util.List;

import com.ssafy.eatnote.model.dto.User;

public interface UserService {
	
	// 회원가입
    void registerUser(User user);

//    // 로그인 (id, password로 사용자 조회)
//    User login(String userId, String userPwd) throws Exception;
    
	// id로 사용자 정보 가져오기
    User getUserById(Long userId);
    
    // email로 사용자 정보 가져오기
    User getUserByEmail(String email);
    
    // 전체 사용자 목록 조회
    List<User> getAllUsers();
    
    // 회원정보 수정
    void updateUser(User user);

    // 회원 탈퇴
    void deleteUser(Long userId);

//	String getPassword(int userSeq);

//	String getUserId(int userSeq);
	
}