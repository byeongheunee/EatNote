package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.TrainerDetails;

public interface TrainerService {
    TrainerDetails getDetailsByUserId(Long userId);
}
