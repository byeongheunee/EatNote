package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.*;
import com.ssafy.eatnote.model.dto.*;
import com.ssafy.eatnote.model.dto.response.CommentResponse;
import com.ssafy.eatnote.model.dto.response.MealFeedbackListViewResponse;
import com.ssafy.eatnote.model.dto.response.MealListViewResponse;
import com.ssafy.eatnote.model.dto.response.MealPublicDetailResponse;
import com.ssafy.eatnote.model.dto.response.MealStatsResponse;
import com.ssafy.eatnote.model.dto.response.TrainerFeedbackResponse;
import com.ssafy.eatnote.model.dto.response.TrainerMealDetailResponse;
import com.ssafy.eatnote.model.scorer.NutritionScorer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealServiceImpl implements MealService {

	private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송
	private final MealImageService mealImageService;
	private final NotificationService notificationService;
	private final FoodInfoDao foodInfoDao;
	private final MealDao mealDao;
	private final MealFeedbackDao mealFeedbackDao;
	private final UserDao userDao;
	private final MemberDao memberDao;
	private final CommentDao commentDao;
	private final FollowDao followDao;
	private final ContentLikeDao contentLikeDao;
	private final NutritionScorer nutritionScorer;
	private final MealFeedbackDao feedbackDao;
	private final ChatGPTService chatGPTService;

	@Transactional
	public Meal saveAnalyzedMeal(MultipartFile file, Long userId, LocalDateTime mealTime) {
	    try {
	    	if (mealTime == null) {
	            mealTime = LocalDateTime.now();
	        }
	        String mealType = classifyMealTimeByHour(mealTime.getHour());

	        Map<String, String> imageInfo = mealImageService.saveMealImage(file, userId, mealType);
	        String imagePath = imageInfo.get("absolutePath");

	        String mealTypeKorean = switch (mealType) {
	            case "breakfast" -> "아침";
	            case "lunch" -> "점심";
	            case "dinner" -> "저녁";
	            case "extra" -> "간식";
	            default -> mealType;
	        };

	        Map<String, Object> result = mealImageService.analyzeMealImage(imagePath);
	        List<Map<String, Object>> detectedList = (List<Map<String, Object>>) result.get("results");

	        float totalCalories = 0f, carb = 0f, protein = 0f, fat = 0f, sugar = 0f, sodium = 0f;
	        List<String> foodNames = new ArrayList<>();

	        for (Map<String, Object> item : detectedList) {
	            String foodCode = (String) item.get("foodCode");
	            String quantity = (String) item.get("quantity");
	            FoodInfo foodInfo = foodInfoDao.selectByFoodCode(foodCode);
	            if (foodInfo == null) continue;

	            float scale = quantityToScale(quantity);
	            totalCalories += foodInfo.getCalories() * scale;
	            carb += foodInfo.getCarbohydrates() * scale;
	            protein += foodInfo.getProtein() * scale;
	            fat += foodInfo.getFat() * scale;
	            sugar += foodInfo.getSugars() * scale;
	            sodium += foodInfo.getSodium() * scale;
	            foodNames.add(foodInfo.getName());
	        }

	        User user = userDao.selectUserById(userId);
	        MemberDetails member = memberDao.findByUserId(userId);
	        String aiFeedback = "분석 중입니다";

	        if (!foodNames.isEmpty()) {
	            float autoScore = nutritionScorer.calculateScore(
	                totalCalories, carb, protein, fat, sugar, sodium, user, member, mealType);

	            String gptPrompt = buildPrompt(user, member, mealTypeKorean, foodNames, carb, protein, fat, sodium, sugar);
	            aiFeedback = chatGPTService.ask(gptPrompt);

	            Meal savedMeal = saveMeal(userId, imageInfo.get("imagePath"), mealType, foodNames, totalCalories, carb,
	                protein, fat, sugar, sodium, autoScore, aiFeedback, mealTime);

	            notifyTrainersOnMealPost(userId, savedMeal);
	            return savedMeal;
	        }

	        // GPT Vision fallback
	        String jsonResponse = chatGPTService.analyzeImageWithGPT(imagePath, user, member, mealTypeKorean);
	        Map<String, Object> gptParsed = chatGPTService.parseJsonResponse(jsonResponse);
	        foodNames = (List<String>) gptParsed.get("foods");

	        if (foodNames == null || foodNames.isEmpty()) {
	            throw new IllegalArgumentException("감지된 음식이 없습니다. 사진을 확인해주세요.");
	        }

	        totalCalories = ((Number) gptParsed.get("calories")).floatValue();
	        carb = ((Number) gptParsed.get("carbohydrates")).floatValue();
	        protein = ((Number) gptParsed.get("protein")).floatValue();
	        fat = ((Number) gptParsed.get("fat")).floatValue();
	        sugar = ((Number) gptParsed.get("sugars")).floatValue();
	        sodium = ((Number) gptParsed.get("sodium")).floatValue();
	        aiFeedback = (String) gptParsed.get("feedback");

	        float autoScore = nutritionScorer.calculateScore(
	            totalCalories, carb, protein, fat, sugar, sodium, user, member, mealType);

	        Meal savedMeal = saveMeal(userId, imageInfo.get("imagePath"), mealType, foodNames, totalCalories, carb,
	            protein, fat, sugar, sodium, autoScore, aiFeedback, mealTime);

	        notifyTrainersOnMealPost(userId, savedMeal);
	        return savedMeal;
	    } catch (IllegalArgumentException e) {
	        throw new RuntimeException("NO_FOOD_DETECTED: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("식사 분석 중 오류 발생", e);
	    }
	}


	private void notifyTrainersOnMealPost(Long userId, Meal savedMeal) {
	    User user = userDao.selectUserById(userId);  // 닉네임 필요

	    List<User> followedTrainers = followDao.findFollowedTrainersByMember(userId);
	    for (User trainer : followedTrainers) {
	        NotificationMessage message = NotificationMessage.builder()
	                .receiverId(trainer.getUserId())
	                .typeCode("NEW_MEAL")
	                .content(user.getNickname() + "님이 식단을 등록했습니다!")
	                .relatedMealId(savedMeal.getMealId())
	                .build();

	        notificationService.saveNotification(message);
	        messagingTemplate.convertAndSend(
	            "/topic/notifications/" + trainer.getUserId(),
	            message
	        );
	    }
	}
	
	public Meal saveMeal(
		    Long userId, String imagePath, String mealType, List<String> foods,
		    float totalCalories, float carb, float protein, float fat, float sugar, float sodium,
		    float autoScore, String aiFeedback, LocalDateTime mealTime
		) {
		    Meal meal = new Meal();
		    meal.setUserId(userId);
		    meal.setImageUrl(imagePath);
		    meal.setMealType(mealType);
		    meal.setDetectedFoods(String.join(", ", foods));
		    meal.setTotalCalories(totalCalories);
		    meal.setCarbohydrates(carb);
		    meal.setProtein(protein);
		    meal.setFat(fat);
		    meal.setSugars(sugar);
		    meal.setSodium(sodium);
		    meal.setAutoScore(autoScore);
		    meal.setAiFeedback(aiFeedback);
		    meal.setMealTime(mealTime); // 👈 추가
		    meal.setCreatedAt(LocalDateTime.now());

		    mealDao.insertMeal(meal);
		    return meal;
		}

	
	public String classifyMealTimeByHour(int hour) {
	    if (hour >= 4 && hour < 11) return "breakfast";
	    if (hour < 16) return "lunch";
	    if (hour < 23) return "dinner";
	    return "extra";
	}

	private String buildPrompt(User user, MemberDetails member, String mealType, List<String> foodNames, float carb,
			float protein, float fat, float sodium, float sugar) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("%s세 %s의 %s 식단을 분석해줘.\n", user.getAge(), user.getGender().equals("M") ? "남성" : "여성",
				mealType));

		sb.append("신체 정보:\n");
		if (member.getHeight() != null)
			sb.append(String.format("- 키: %.1fcm\n", member.getHeight()));
		if (member.getWeight() != null)
			sb.append(String.format("- 몸무게: %.1fkg\n", member.getWeight()));
		if (member.getBmi() != null)
			sb.append(String.format("- BMI: %.1f\n", member.getBmi()));
		if (member.getBodyFatPercentage() != null)
			sb.append(String.format("- 체지방률: %.1f%%\n", member.getBodyFatPercentage()));
		if (member.getBodyFatMass() != null)
			sb.append(String.format("- 체지방량: %.1fg\n", member.getBodyFatMass()));
		if (member.getSkeletalMuscle() != null)
			sb.append(String.format("- 골격근량: %.1fg\n", member.getSkeletalMuscle()));
		if (member.getProtein() != null)
			sb.append(String.format("- 단백질량: %.1fg\n", member.getProtein()));
		if (member.getGoal() != null)
			sb.append(String.format("- 목표: %s\n", member.getGoal()));

		sb.append("\n식단:\n");
		sb.append("- 음식: ").append(String.join(", ", foodNames)).append("\n");
		sb.append(String.format("- 탄수화물: %.1fg\n", carb));
		sb.append(String.format("- 단백질: %.1fg\n", protein));
		sb.append(String.format("- 지방: %.1fg\n", fat));
		sb.append(String.format("- 나트륨: %.2fg\n", sodium));
		sb.append(String.format("- 당류: %.1fg\n", sugar));
		sb.append("신체정보와 목표, 음식 정보에 따라 객관적으로 3~4줄의 상 피드백 만들어줘");
		return sb.toString();
	}

	private float quantityToScale(String quantity) {
		switch (quantity) {
		case "Q1":
			return 0.2f;
		case "Q2":
			return 0.4f;
		case "Q3":
			return 0.6f;
		case "Q4":
			return 0.8f;
		case "Q5":
			return 1.0f;
		default:
			return 0.0f;
		}
	}

	@Override
	public MealPublicDetailResponse getPublicMealDetail(Long mealId, Long loginUserId) {
	    Meal meal = mealDao.findById(mealId);
	    String nickName = userDao.findNicknameByUserId(meal.getUserId());
	    if (meal == null)
	        throw new RuntimeException("❌ 해당 식단을 찾을 수 없습니다.");

	    // 댓글 조회 및 myReaction 포함 트리 구성
	    List<Comment> commentEntities = commentDao.getCommentsByTarget("MEAL", mealId);
	    List<CommentResponse> commentTree = buildCommentTree(commentEntities, loginUserId); // <-- myReaction 포함된 방식

	    // 내 반응 상태 조회
	    String myReaction = null;
	    if (loginUserId != null) {
	        ContentLike like = contentLikeDao.selectByUserAndContent(loginUserId, "MEAL", mealId);
	        if (like != null) {
	            myReaction = like.getLikeType(); // LIKE / DISLIKE
	        }
	    }
	    
	    MealFeedback latestFeedback = feedbackDao.findLatestFeedbackByMealId(mealId);
	    String trainerFeedback = (latestFeedback != null) ? latestFeedback.getContent() : null;
	    Float trainerScore = (latestFeedback != null) ? latestFeedback.getTrainerScore() : null;

	    return MealPublicDetailResponse.builder()
	            .mealId(meal.getMealId())
	            .userId(meal.getUserId())
	            .userNickname(nickName)
	            .mealType(meal.getMealType())
	            .mealTime(meal.getMealTime())
	            .detectedFoods(meal.getDetectedFoods())
	            .totalCalories(meal.getTotalCalories())
	            .carbohydrates(meal.getCarbohydrates())
	            .protein(meal.getProtein())
	            .fat(meal.getFat())
	            .sugars(meal.getSugars())
	            .sodium(meal.getSodium())
	            .imageUrl(meal.getImageUrl())
	            .likeCount(meal.getLikeCount())
	            .dislikeCount(meal.getDislikeCount())
	            .autoScore(meal.getAutoScore())
	            .aiFeedback(meal.getAiFeedback())
	            .trainerFeedback(trainerFeedback)      
	            .trainerScore(trainerScore)
	            .myReaction(myReaction)
	            .comments(commentTree)
	            .build();
	}
	
	private List<CommentResponse> buildCommentTree(List<Comment> comments, Long loginUserId) {
	    List<CommentResponse> roots = new ArrayList<>();
	    Stack<CommentResponse> stack = new Stack<>();

	    for (Comment comment : comments) {
	        // 좋아요/싫어요 수는 캐싱된 값 사용
	        int likeCount = comment.getLikeCount();
	        int dislikeCount = comment.getDislikeCount();

	        // 로그인 사용자의 반응 조회
	        String myReaction = null;
	        if (loginUserId != null) {
	            ContentLike like = contentLikeDao.selectByUserAndContent(loginUserId, "COMMENT", Long.valueOf(comment.getCommentId()));
	            if (like != null) {
	                myReaction = like.getLikeType(); // LIKE / DISLIKE
	            }
	        }

	        CommentResponse node = CommentResponse.builder()
	                .commentId(comment.getCommentId())
	                .content(comment.getContent())
	                .userId(comment.getUserId())
	                .userNickname(comment.getUserNickname())
	                .createdAt(comment.getCreatedAt())
	                .updatedAt(comment.getUpdatedAt())
	                .depth(comment.getDepth())
	                .likeCount(likeCount)
	                .dislikeCount(dislikeCount)
	                .myReaction(myReaction)
	                .replies(new ArrayList<>())
	                .build();

	        while (!stack.isEmpty() && comment.getDepth() <= stack.peek().getDepth()) {
	            stack.pop();
	        }

	        if (stack.isEmpty()) {
	            roots.add(node);
	        } else {
	            stack.peek().getReplies().add(node);
	        }

	        stack.push(node);
	    }

	    return roots;
	}

	@Override
	public List<TrainerFeedbackResponse> getFeedbackListByMealId(Long mealId, Long loginUserId)
	{
	    List<MealFeedback> feedbacks = feedbackDao.findAllByMealId(mealId);

	    return feedbacks.stream().map(fb -> TrainerFeedbackResponse.builder()
	            .feedbackId(fb.getFeedbackId())
	            .trainerId(fb.getTrainerId())
	            .trainerNickname(userDao.findNicknameByUserId(fb.getTrainerId()))
	            .trainerScore(fb.getTrainerScore())
	            .comment(fb.getContent())
	            .createdAt(fb.getCreatedAt())
	            .isWrittenByMe(fb.getTrainerId().equals(loginUserId))
	            .build()
	    ).collect(Collectors.toList());
	}


	@Override
	public List<MealListViewResponse> getMealsForViewer(Long targetUserId, Long requesterId) {
	    // 1. DB에서 미리 매핑된 DTO 가져옴 (likeCount 포함되어 있어야 함)
	    List<MealListViewResponse> meals = mealDao.findMealsByUserId(targetUserId);

	    // 2. 요청자 정보 확인
	    User requester = userDao.selectUserById(requesterId);
	    boolean isTrainer = requester.getUserType() == 1;

	    for (MealListViewResponse meal : meals) {
	        Long mealId = meal.getMealId();

	        // 내 반응 상태 추가
	        ContentLike like = contentLikeDao.selectByUserAndContent(requesterId, "MEAL", mealId);
	        meal.setMyReaction(like != null ? like.getLikeType() : null);

	        // 트레이너 피드백 여부
	        if (isTrainer) {
	            boolean wroteFeedback = feedbackDao.existsByTrainerAndMeal(requesterId, mealId);
	            meal.setIsFeedbackedByMe(wroteFeedback);
	        }
	    }

	    return meals;
	}

	@Override
	public void deleteMealById(Long mealId, Long userId) {
		Meal meal = mealDao.findById(mealId);
		if (meal == null) {
			throw new RuntimeException("삭제할 식단을 찾을 수 없습니다.");
		}

		if (!meal.getUserId().equals(userId)) {
			throw new RuntimeException("본인이 작성한 식단만 삭제할 수 있습니다.");
		}

		mealDao.deleteMealById(mealId);
	}

	@Override
	public TrainerMealDetailResponse getTrainerMealDetail(Long mealId, Long trainerId) {
		Meal meal = mealDao.findById(mealId); 

		return TrainerMealDetailResponse.builder().mealId(meal.getMealId()).mealType(meal.getMealType())
				.mealTime(meal.getMealTime()).detectedFoods(meal.getDetectedFoods()).autoScore(meal.getAutoScore())
				.totalCalories(meal.getTotalCalories()).carbohydrates(meal.getCarbohydrates())
				.protein(meal.getProtein()).fat(meal.getFat()).sugars(meal.getSugars()).sodium(meal.getSodium())
				.imageUrl(meal.getImageUrl()).userId(meal.getUserId())
				.build();
	}
	
	@Override
    public List<MealListViewResponse> getRecentMeals() {
        return mealDao.findRecentMeals();
    }

    @Override
    public List<MealListViewResponse> getPopularMealsThisWeek() {
        return mealDao.findPopularMealsInLast7Days();
    }
    
    @Override
    public List<MealListViewResponse> getMealsOfMyFollowings(Long userId) {
        List<User> followings = followDao.selectFollowing(userId);
        List<Long> userIds = followings.stream().map(User::getUserId).toList();
        
        List<MealListViewResponse> resultList = mealDao.findMealsByUserIds(userIds);
        
        for (MealListViewResponse meal : resultList) {
	        Long mealId = meal.getMealId();

	        // 내 반응 상태 추가
	        ContentLike like = contentLikeDao.selectByUserAndContent(userId, "MEAL", mealId);
	        meal.setMyReaction(like != null ? like.getLikeType() : null);

	    }
        
        return resultList;
    }

    @Override
    public MealStatsResponse getMealStatistics(Long userId) {
    	// 연속 기록일
    	List<LocalDate> mealDates = mealDao.findMealDatesByUserId(userId);
    	int consecutiveDays = calculateConsecutiveDays(mealDates);
    	// 총 식단 기록
    	int totalMeals = mealDao.countByUserId(userId);
    	// 피드백 받은 식단 수
        int feedbackReceived = mealFeedbackDao.countByUserMeals(userId);
        // 평균 칼로리
        int averageCalories = mealDao.calculateAverageCalories(userId);
        
        return MealStatsResponse.builder()
                .totalMeals(totalMeals)
                .feedbackReceived(feedbackReceived)
                .averageCalories(averageCalories)
                .consecutiveDays(consecutiveDays)
                .build();
    }
    
    public int calculateConsecutiveDays(List<LocalDate> mealDates) {
        Set<LocalDate> dateSet = new HashSet<>(mealDates);

        // 오늘 식단이 있는지 확인
        LocalDate today = LocalDate.now();
        boolean hasToday = dateSet.contains(today);

        // 시작일: 오늘 포함 여부에 따라 다르게 설정
        LocalDate startDay = hasToday ? today : today.minusDays(1);

        int count = 0;
        LocalDate current = startDay;

        // 연속되는 날짜를 역순으로 체크
        while (dateSet.contains(current)) {
            count++;
            current = current.minusDays(1);
        }

        return count;
    }
    
    @Override
    public List<MealListViewResponse> getRecentMealsByUserId(Long userId) {
        return mealDao.findRecentMealsByUserId(userId);
    }

    @Override
    public List<MealFeedbackListViewResponse> getRecentFeedbacksByUserId(Long userId) {
        return mealFeedbackDao.findRecentFeedbacksByUserId(userId);
    }
}
