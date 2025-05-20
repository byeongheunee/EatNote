package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.MemberDetails;

public interface MemberService {
	
	// 회원 디테일 정보 조회
	MemberDetails getDetailsByUserId(Long userId);
	
	// 회원 목표 조회
	String getGoalByUserId(Long userId);
}
