package com.ssafy.eatnote.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ssafy.eatnote.model.dto.FeedbackCalendarStat;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.response.FolloweeFeedbackSummary;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;

public interface TrainerService {
	
	 
    TrainerDetails getDetailsByUserId(Long userId);
    
    List<MealListViewResponse> getPendingFeedbackMeals(Long trainerId);
    
    Map<String, FeedbackCalendarStat> getFeedbackCalendarStats(Long trainerId, LocalDate startDate, LocalDate endDate);

   List<FolloweeFeedbackSummary> getFolloweeFeedbackSummaries(Long trainerId);
}
