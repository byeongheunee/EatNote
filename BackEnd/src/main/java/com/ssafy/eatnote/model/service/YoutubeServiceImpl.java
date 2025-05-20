package com.ssafy.eatnote.model.service;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.ssafy.eatnote.model.dto.VideoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeServiceImpl implements YoutubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    @Override
    public List<VideoDto> searchVideos(String keyword) {
        try {
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    jsonFactory,
                    request -> {}
            ).setApplicationName("EatNoteApp").build();

            YouTube.Search.List search = youtube.search()
            	    .list(Collections.singletonList("snippet"))
            	    .setKey(apiKey)
            	    .setQ(keyword)
            	    .setType(Collections.singletonList("video"))
            	    .setMaxResults(10L);

            SearchListResponse response = search.execute();
            List<SearchResult> results = response.getItems();

            List<VideoDto> dtoList = new ArrayList<>();
            for (SearchResult item : results) {
                if (!item.getId().getKind().equals("youtube#video")) continue;

                String videoId = item.getId().getVideoId();
                String title = item.getSnippet().getTitle();
                String thumbnailUrl = item.getSnippet().getThumbnails().getDefault().getUrl();
                String channelTitle = item.getSnippet().getChannelTitle();
                String publishedAt = item.getSnippet().getPublishedAt().toStringRfc3339();

                dtoList.add(VideoDto.builder()
                        .title(title)
                        .videoId(videoId)
                        .thumbnailUrl(thumbnailUrl)
                        .channelTitle(channelTitle)
                        .publishedAt(publishedAt)
                        .url("https://www.youtube.com/watch?v=" + videoId)
                        .build());
            }
            return dtoList;

        } catch (IOException e) {
            throw new RuntimeException("YouTube API 요청 실패", e);
        }
    }
}
