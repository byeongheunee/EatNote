package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.TrainerDetails;

@Mapper
public interface TrainerDao {
	
	// 트레이너 정보 입력
	boolean insertTrainerDetails(TrainerDetails details);
	
	// 트레이너 상세 정보 조회
	TrainerDetails findByUserId(Long userId);
	
	// 트레이너 상세 정보 수정
	void updateTrainerDetails(TrainerDetails details);
}
