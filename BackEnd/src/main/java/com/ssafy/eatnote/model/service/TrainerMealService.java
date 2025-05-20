package com.ssafy.eatnote.model.service;

import java.util.List;

import com.ssafy.eatnote.model.dto.response.DailyNutritionStatsResponse;
import com.ssafy.eatnote.model.dto.response.TrainerMealDetailResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyFeedbackResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyNutritionStatsResponse;

public interface TrainerMealService {
	
    
    TrainerMealDetailResponse getMealDetailById(Long mealId);
    
    List<WeeklyNutritionStatsResponse> getWeeklyStats(Long userId);
    
    List<DailyNutritionStatsResponse> getDailyStats(Long userId);

	WeeklyFeedbackResponse getWeeklyGptFeedback(Long userId);
	
	boolean hasTrainerWrittenFeedback(Long mealId, Long trainerId);
}

