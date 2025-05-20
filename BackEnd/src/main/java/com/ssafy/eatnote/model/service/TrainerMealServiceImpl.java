package com.ssafy.eatnote.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.MealDao;
import com.ssafy.eatnote.model.dao.MealFeedbackDao;
import com.ssafy.eatnote.model.dao.MemberDao;
import com.ssafy.eatnote.model.dao.UserDao;
import com.ssafy.eatnote.model.dto.MemberDetails;
import com.ssafy.eatnote.model.dto.User;
import com.ssafy.eatnote.model.dto.response.DailyNutritionStatsResponse;
import com.ssafy.eatnote.model.dto.response.TrainerMealDetailResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyFeedbackResponse;
import com.ssafy.eatnote.model.dto.response.WeeklyNutritionStatsResponse;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class TrainerMealServiceImpl implements TrainerMealService {

	 private final MealDao mealDao;
	 private final UserDao userDao;
	 private final ChatGPTService chatGPTService;
	 private final MemberDao memberDao;
	 private final MealFeedbackDao feedbackDao;


	    
	    @Override
	    public TrainerMealDetailResponse getMealDetailById(Long mealId) {
	        return mealDao.selectTrainerMealDetailById(mealId);
	    }

	    @Override
	    public List<WeeklyNutritionStatsResponse> getWeeklyStats(Long userId) {
	        return mealDao.getWeeklyStatsByUserId(userId);
	    }
	    
	    @Override
	    public List<DailyNutritionStatsResponse> getDailyStats(Long userId) {
	        return mealDao.findDailyStatsByUserId(userId);
	    }

	    @Override
	    public WeeklyFeedbackResponse getWeeklyGptFeedback(Long userId){
	        try {
	    	// 1. 최신 주차 식단 통계
	        List<WeeklyNutritionStatsResponse> statsList = mealDao.getWeeklyStatsByUserId(userId);
	        
	        if (statsList == null || statsList.isEmpty()) {
	            throw new RuntimeException("이번 주 식사 기록이 존재하지 않습니다.");
	        }
	        
	        WeeklyNutritionStatsResponse latestWeek = statsList.get(0);

	        // 2. 사용자 정보
	        User user = userDao.selectUserById(userId);
	        MemberDetails member = memberDao.findByUserId(userId);

	        // 3. GPT 호출
	        return chatGPTService.generateWeeklyNutritionFeedback(user, member, latestWeek);
	        } catch (IOException e) {
	            throw new RuntimeException("GPT 피드백 생성 중 오류 발생", e);
	        }
	    }

	    @Override
	    public boolean hasTrainerWrittenFeedback(Long mealId, Long trainerId) {
	        return feedbackDao.existsByTrainerAndMeal(trainerId, mealId);
	    }
	
}
