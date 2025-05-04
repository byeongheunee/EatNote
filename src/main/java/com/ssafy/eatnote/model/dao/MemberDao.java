package com.ssafy.eatnote.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.eatnote.model.dto.MemberDetails;

@Mapper
public interface MemberDao {
	boolean insertMemberDetails(MemberDetails details);
}
