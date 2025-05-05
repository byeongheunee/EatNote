package com.ssafy.eatnote.model.service;

import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.TrainerDao;
import com.ssafy.eatnote.model.dto.TrainerDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;

    @Override
    public TrainerDetails getDetailsByUserId(Long userId) {
        return trainerDao.findByUserId(userId);
    }
}