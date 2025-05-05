package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.MemberDetails;

public interface MemberService {
	MemberDetails getDetailsByUserId(Long userId);
}
