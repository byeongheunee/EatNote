package com.ssafy.eatnote.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.ssafy.eatnote.model.dto.FeedbackCalendarStat;
import com.ssafy.eatnote.model.dto.Meal;
import com.ssafy.eatnote.model.dto.response.DailyNutritionStatsResponse;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.TrainerMealDetailResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyNutritionStatsResponse;

@Mapper
public interface MealDao {
	
    void insertMeal(Meal meal);
    
    Meal findById(Long mealId);
    
    List<MealListViewResponse> findMealsByUserId(@Param("userId") Long userId);
    
    TrainerMealDetailResponse selectTrainerMealDetailById(@Param("mealId") Long mealId);

    List<WeeklyNutritionStatsResponse> getWeeklyStatsByUserId(@Param("userId") Long userId);

    void deleteMealById(@Param("mealId") Long mealId);
    
    List<DailyNutritionStatsResponse> findDailyStatsByUserId(@Param("userId") Long userId);
    
    List<MealListViewResponse> findRecentMeals();

    List<MealListViewResponse> findPopularMealsInLast7Days();
    
    List<MealListViewResponse> findMealsByUserIds(@Param("userIds") List<Long> userIds);
    
    List<MealListViewResponse> findMealsWithoutFeedback(@Param("trainerId") Long trainerId);
    
    List<FeedbackCalendarStat> getFeedbackCalendarStat(@Param("trainerId") Long trainerId, @Param("month") String month);

    List<LocalDate> findMealDatesByUserId(@Param("userId") Long userId);
    
    int countByUserId(@Param("userId") Long userId);
    
    int calculateAverageCalories(@Param("userId") Long userId);
    
    List<MealListViewResponse> findRecentMealsByUserId(@Param("userId") Long userId);
    
}
