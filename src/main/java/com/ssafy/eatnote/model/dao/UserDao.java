package com.ssafy.eatnote.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.User;

@Mapper
public interface UserDao {

	// 회원가입
	boolean insertUser(User user);

//    // 로그인 (userId, userPwd로 조회)
//    User selectUserByIdAndPwd(String userId, String userPwd) throws Exception;

	// userId로 사용자 조회
	User selectUserBySeq(Long userId);

	// email로 사용자 조회
	User selectUserByEmail(String email);

	// 전체 사용자 목록 조회
	List<User> selectAllUsers();

	// 회원정보 수정
	boolean updateUser(User user);

	// userId로 사용자 삭제
	boolean deleteUserById(Long userId);
	
	// email 확인
	boolean existsByEmail(String email);

	// 닉네임 중복 체크
	int countByNickname(String nickname);
}
