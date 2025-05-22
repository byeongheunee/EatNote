package com.ssafy.eatnote.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.request.CommonUserUpdateRequest;
import com.ssafy.eatnote.model.dto.request.MemberUpdateRequest;
import com.ssafy.eatnote.model.dto.request.TrainerUpdateRequest;
import com.ssafy.eatnote.model.dto.request.UserRegisterRequest;
import com.ssafy.eatnote.model.dto.response.MemberProfileResponse;
import com.ssafy.eatnote.model.dto.response.TrainerProfileResponse;

public interface UserService {
	
	/**
     * 회원가입 처리 - 유저 기본 정보 및 상세 정보 저장
     *
     * @param request 사용자 정보 + 상세 정보
     * @param file 프로필 이미지 파일 (nullable)
     * @return 성공 여부
     */
	boolean registerUser(UserRegisterRequest request, MultipartFile profileImageFile, MultipartFile certFile);

	// id로 사용자 정보 가져오기
    User getUserById(Long userId);
    
    // email로 사용자 정보 가져오기
    User getUserByEmail(String email);
    
    // 전체 사용자 목록 조회
    List<User> getAllUsers();

    // 회원 탈퇴
    boolean deleteUser(Long userId);
    
    // email 확인
    boolean isEmailDuplicated(String email);
    
    // 닉네임 중복 체크
    boolean isNicknameAvailable(String nickname);
    
    // 닉네임 중복 체크(본인 닉네임 제외)
    boolean isMyNicknameOrAvailable(Long userId, String nickname);
	
    // 비밀번호 확인
    boolean checkPassword(Long userId, String inputPassword);
    
    // 회원 정보 수정
    void updateMember(Long userId, CommonUserUpdateRequest common, MemberUpdateRequest member, MultipartFile file);
    
    // 트레이너 정보 수정
    void updateTrainer(Long userId, CommonUserUpdateRequest common, TrainerUpdateRequest trainerRequest, MultipartFile profileImageFile, MultipartFile certFile);    
    
    // 회원 탈퇴로 상태 변경
    void markUserAsDeleted(Long userId);
    
    // 회원 프로필 조회
    MemberProfileResponse getMemberProfile(Long viewerId, Long userId);
    
    // 트레이너 프로필 조회
    TrainerProfileResponse getTrainerProfile(Long viewerId, Long userId);
    
    // 회원의 유형 조회
    Integer getUserType(Long userId);
    
    // 트레이너 이름 조회
    boolean existsTrainerNickname(String nickname);
}