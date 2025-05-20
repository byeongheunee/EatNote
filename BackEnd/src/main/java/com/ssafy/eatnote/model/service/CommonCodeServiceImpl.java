package com.ssafy.eatnote.model.service;

import com.ssafy.eatnote.model.dao.CommonCodeDao;
import com.ssafy.eatnote.model.dto.CommonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommonCodeServiceImpl implements CommonCodeService {

    private final CommonCodeDao commonCodeDao;

    @Override
    public Map<String, List<CommonCode>> getAllCommonCodes() {
        Map<String, List<CommonCode>> map = new HashMap<>();
        map.put("gender", getGenderCodes());
        map.put("userStatus", getUserStatusCodes());
        map.put("userType", getUserTypeCodes());
        map.put("goal", getGoalCodes());
        map.put("followStatus", getFollowStatusCodes());
        map.put("notificationType", getNotificationTypeCodes());
        map.put("boardAccess", getBoardAccessLevelCodes());
        map.put("commentTargetType", getCommentTargetTypeCodes());
        map.put("likeType", getLikeTypeCodes());
        return map;
    }

    @Override public List<CommonCode> getGenderCodes() { return commonCodeDao.selectGenderCodes(); }
    @Override public List<CommonCode> getUserStatusCodes() { return commonCodeDao.selectUserStatusCodes(); }
    @Override public List<CommonCode> getUserTypeCodes() { return commonCodeDao.selectUserTypeCodes(); }
    @Override public List<CommonCode> getGoalCodes() { return commonCodeDao.selectGoalCodes(); }
    @Override public List<CommonCode> getFollowStatusCodes() { return commonCodeDao.selectFollowStatusCodes(); }
    @Override public List<CommonCode> getNotificationTypeCodes() { return commonCodeDao.selectNotificationTypeCodes(); }
    @Override public List<CommonCode> getBoardAccessLevelCodes() { return commonCodeDao.selectBoardAccessLevelCodes(); }
    @Override public List<CommonCode> getCommentTargetTypeCodes() { return commonCodeDao.selectCommentTargetTypeCodes(); }
    @Override public List<CommonCode> getLikeTypeCodes() { return commonCodeDao.selectLikeTypeCodes(); }
}
