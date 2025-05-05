package com.ssafy.eatnote.model.service;

import org.springframework.stereotype.Service;

import com.ssafy.eatnote.model.dao.MemberDao;
import com.ssafy.eatnote.model.dto.MemberDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;

    @Override
    public MemberDetails getDetailsByUserId(Long userId) {
        return memberDao.findByUserId(userId);
    }
}