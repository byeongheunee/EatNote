package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.FoodInfo;

@Mapper
public interface FoodInfoDao {
    FoodInfo selectByFoodCode(String foodCode);
}
