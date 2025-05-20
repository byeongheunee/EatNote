package com.ssafy.eatnote.model.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowerMealResponse {

    private Long mealId;
    private Long userId;
    private String nickname;
    private String profileImage;
    private String mealType;
    private LocalDateTime mealTime;
    private String detectedFoods;
    private Float totalCalories;
    private Float autoScore;
    private String imageUrl;
    private Integer likeCount;
    private Integer commentCount;
    private String myReaction; // LIKE / DISLIKE
}
