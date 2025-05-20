package com.ssafy.eatnote.model.dao;

import com.ssafy.eatnote.model.dto.CommonCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommonCodeDao {
    List<CommonCode> selectGenderCodes();
    List<CommonCode> selectUserStatusCodes();
    List<CommonCode> selectUserTypeCodes();
    List<CommonCode> selectGoalCodes();
    List<CommonCode> selectFollowStatusCodes();
    List<CommonCode> selectNotificationTypeCodes();
    List<CommonCode> selectBoardAccessLevelCodes();
    List<CommonCode> selectCommentTargetTypeCodes();
    List<CommonCode> selectLikeTypeCodes();
}
