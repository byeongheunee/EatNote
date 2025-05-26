package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.Meal;
import com.ssafy.eatnote.model.dto.response.MealFeedbackListViewResponse;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.MealPublicDetailResponse;
import com.ssafy.eatnote.model.dto.response.MealStatsResponse;
import com.ssafy.eatnote.model.dto.response.TrainerFeedbackResponse;
import com.ssafy.eatnote.model.dto.response.TrainerMealDetailResponse;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MealService {

    // 사진을 저장하고, Flask AI 분석 결과로 DB 저장까지 처리하는 메서드
    Meal saveAnalyzedMeal(MultipartFile file, Long userId, LocalDateTime mealTime);
    
    // 식사 관련 상세 정보 가저오는 메서드 
    MealPublicDetailResponse getPublicMealDetail(Long mealId, Long loginUserId);
    
    // 식단 번호를 바탕으로 트레이너 피드백을 모두 가져오는 메서드 
    List<TrainerFeedbackResponse> getFeedbackListByMealId(Long mealId, Long loginUserId);
    
    // 식단 리스트를 찾아 볼 수 있게 하는 메서드
    List<MealListViewResponse> getMealsForViewer(Long targetUserId, Long requesterId);
    
    // 식단 삭제하기 
    void deleteMealById(Long mealId, Long userId);
    
    // 트레이너 식단 상세 조회
    TrainerMealDetailResponse getTrainerMealDetail(Long mealId, Long trainerId);
    
    // 최근 식단 10개 조회 
    List<MealListViewResponse> getRecentMeals();
    
    // 인기있는 식단 10개 조회 
    List<MealListViewResponse> getPopularMealsThisWeek();
    
    // 팔로우하는 사람 식단 조회
    List<MealListViewResponse> getMealsOfMyFollowings(Long userId);
    
    // 식단 통계 수치
    MealStatsResponse getMealStatistics(Long userId);
    
    // 특정 회원의 최근 식단 정보 20개 조회
    List<MealListViewResponse> getRecentMealsByUserId(Long userId);
    
    List<MealFeedbackListViewResponse> getRecentFeedbacksByUserId(Long userId);
    
}
