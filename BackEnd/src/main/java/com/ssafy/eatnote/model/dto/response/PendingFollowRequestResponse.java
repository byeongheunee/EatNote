package com.ssafy.eatnote.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingFollowRequestResponse {
    private Long userId;
    private String nickname;
    private String gender;
    private String profileImage;
    private LocalDateTime requestedAt;
    private Long followId;
    
}
