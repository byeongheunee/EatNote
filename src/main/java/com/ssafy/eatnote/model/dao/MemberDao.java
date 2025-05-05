package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.MemberDetails;

@Mapper
public interface MemberDao {
	
	// 회원 정보 입력
	boolean insertMemberDetails(MemberDetails details);
	
	// 회원 상세 정보 조회
	MemberDetails findByUserId(Long userId);
}
