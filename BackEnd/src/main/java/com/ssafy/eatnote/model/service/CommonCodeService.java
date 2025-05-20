package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dto.CommonCode;

import java.util.List;
import java.util.Map;

public interface CommonCodeService {
    Map<String, List<CommonCode>> getAllCommonCodes();

    List<CommonCode> getGenderCodes();
    List<CommonCode> getUserStatusCodes();
    List<CommonCode> getUserTypeCodes();
    List<CommonCode> getGoalCodes();
    List<CommonCode> getFollowStatusCodes();
    List<CommonCode> getNotificationTypeCodes();
    List<CommonCode> getBoardAccessLevelCodes();
    List<CommonCode> getCommentTargetTypeCodes();
    List<CommonCode> getLikeTypeCodes();
}
