package com.ssafy.eatnote.model.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.eatnote.model.dto.User;

@Mapper
public interface UserDao {

	// 회원가입
	boolean insertUser(User user);

	// userId로 사용자 조회
	User selectUserById(Long userId);

	// email로 사용자 조회
	User selectUserByEmail(String email);

	// 전체 사용자 목록 조회
	List<User> selectAllUsers();

	// 유저 공통 정보 수정
	void updateUserProfile(Long userId, String password, String nickname, String profileImage);

	// userId로 사용자 삭제
	boolean deleteUserById(Long userId);
	
	// email 확인
	boolean existsByEmail(String email);

	// 닉네임 중복 체크
	int countByNickname(String nickname);
	
	// userId로 닉네임 조회
	String findNicknameByUserId(Long userId);
	
    /**
     * 닉네임으로 유저 조회
     * @param nickname 닉네임
     * @return Optional<User>
     */
    Optional<User> findByNickname(@Param("nickname") String nickname);
    // Optional<User>는 MyBatis가 자동으로 null을 감싸서 Optional로 리턴할 수 있게 spring-boot-starter에서 처리
    
    
    /**
     * 해당 유저가 트레이너인지 여부 조회
     * @param userId 사용자 ID
     * @return true: 트레이너 / false: 일반회원
     */
    boolean isTrainer(@Param("userId") Long userId);
    
    // 회원 유형 조회
    Integer selectUserTypeByUserId(Long userId);
    
    /**
     * 사용자 상태 변경 (ex: ACTIVE → DELETED)
     * @param userId 대상 사용자 ID
     * @param status 변경할 상태 코드
     * @return 변경된 행 수
     */
    int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);
    
}
