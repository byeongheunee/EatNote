package com.ssafy.eatnote.model.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ssafy.eatnote.model.dto.FeedbackCalendarStat;
import com.ssafy.eatnote.model.dto.TrainerDetails;
import com.ssafy.eatnote.model.dto.response.FolloweeFeedbackSummary;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.PendingFollowRequestResponse;

public interface TrainerService {
	
	 
    TrainerDetails getDetailsByUserId(Long userId);
    
    List<MealListViewResponse> getPendingFeedbackMeals(Long trainerId);

   List<FolloweeFeedbackSummary> getFolloweeFeedbackSummaries(Long trainerId);
   
   List<PendingFollowRequestResponse> getPendingFollowRequests(Long trainerId);
   
   List<FeedbackCalendarStat> getFeedbackCalendarStats(Long trainerId, String month);

}
