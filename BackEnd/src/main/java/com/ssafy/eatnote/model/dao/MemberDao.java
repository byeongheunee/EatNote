package com.ssafy.eatnote.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.eatnote.model.dto.MemberDetails;

@Mapper
public interface MemberDao {
	
	// 회원 정보 입력
	boolean insertMemberDetails(MemberDetails details);
	
	// 회원 상세 정보 조회
	MemberDetails findByUserId(Long userId);

	// 알레르기 정보 입력 (단건 입력)
	boolean insertUserAllergy(@Param("userId") Long userId, @Param("allergyId") Integer allergyId);
	
	// 회원 상세 정보 조회 수정
	void updateMemberDetails(MemberDetails details);
	
	// 알레르기 정보 삭제 (userId 해당 아이디에 있는 알레르기 정보 모두 삭제)
	void deleteUserAllergies(Long userId);
	
	// 알레르기 정보 입력 (다중 입력)
	void insertUserAllergies(@Param("userId") Long userId, @Param("allergyIds") List<Integer> allergyIds);
	
	// 회원 목표 조회
	String findGoalByUserId(Long userId);
}
