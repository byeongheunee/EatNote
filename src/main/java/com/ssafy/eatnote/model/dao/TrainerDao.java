package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.TrainerDetails;

@Mapper
public interface TrainerDao {
	boolean insertTrainerDetails(TrainerDetails details);
}
