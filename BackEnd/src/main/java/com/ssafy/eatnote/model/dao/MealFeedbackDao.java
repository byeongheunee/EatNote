package com.ssafy.eatnote.model.dao;

import com.ssafy.eatnote.model.dto.MealFeedback;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MealFeedbackDao {
	
    void insertFeedback(MealFeedback feedback);
    
    MealFeedback findByMealId(Long mealId);
    
    MealFeedback findById(@Param("feedbackId") Long feedbackId);
    
    List<MealFeedback> findAllByMealId(@Param("mealId") Long mealId);
    
    boolean existsByTrainerAndMeal(@Param("trainerId") Long trainerId, @Param("mealId") Long mealId);

	void deleteFeedback(Long feedbackId);

	void updateFeedback(MealFeedback existing);
}
