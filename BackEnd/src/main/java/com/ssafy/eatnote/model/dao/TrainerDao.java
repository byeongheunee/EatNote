package com.ssafy.eatnote.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.response.FolloweeFeedbackSummary;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface TrainerDao {
	
	// 트레이너 정보 입력
	boolean insertTrainerDetails(TrainerDetails details);
	
	// 트레이너 상세 정보 조회
	TrainerDetails findByUserId(Long userId);
	
	// 트레이너 상세 정보 수정
	void updateTrainerDetails(TrainerDetails details);
	
	List<FolloweeFeedbackSummary> getFolloweeFeedbackSummary(@Param("trainerId") Long trainerId);
	
}
