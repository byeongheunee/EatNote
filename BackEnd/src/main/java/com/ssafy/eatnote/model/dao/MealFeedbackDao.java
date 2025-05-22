package com.ssafy.eatnote.model.dao;

import com.ssafy.eatnote.model.dto.MealFeedback;
import com.ssafy.eatnote.model.dto.response.MealFeedbackListViewResponse;

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
	
	MealFeedback findLatestFeedbackByMealId(Long mealId);
	
	int countByUserMeals(@Param("userId") Long userId);

	List<MealFeedbackListViewResponse> findRecentFeedbacksByUserId(@Param("userId") Long userId);
	
	// 트레이너가 작성한 피드백 수
	int countByTrainerId(@Param("trainerId") Long trainerId);
}
