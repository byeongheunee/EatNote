package com.ssafy.eatnote.model.service;

import java.util.List;

import com.ssafy.eatnote.model.dto.VideoDto;

public interface YoutubeService {
    List<VideoDto> searchVideos(String keyword);
}