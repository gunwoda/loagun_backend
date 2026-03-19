package com.loagun.backend.notice.service;

import com.loagun.backend.global.client.LostarkApiClient;
import com.loagun.backend.notice.dto.EventResponse;
import com.loagun.backend.notice.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final LostarkApiClient lostarkApiClient;

    /**
     * 공지사항 조회
     * - type: "공지" | "점검" | "상점" | "이벤트" (null이면 전체)
     * - searchText: 제목 검색 (null이면 전체)
     * - TTL 10분: 공지는 실시간성이 낮아 자주 변경되지 않음
     */
    @Cacheable(value = "notices", key = "'notices:' + #type + ':' + #searchText")
    public List<NoticeResponse> getNotices(String type, String searchText) {
        log.debug("공지사항 조회 - API 호출: type={}, searchText={}", type, searchText);
        NoticeResponse[] response = lostarkApiClient.get(
                uriBuilder -> {
                    var builder = uriBuilder.path("/news/notices");
                    if (type != null && !type.isBlank()) builder = builder.queryParam("type", type);
                    if (searchText != null && !searchText.isBlank()) builder = builder.queryParam("searchText", searchText);
                    return builder.build();
                },
                NoticeResponse[].class
        );
        return response != null ? Arrays.asList(response) : List.of();
    }

    /**
     * 진행 중인 이벤트 목록 조회
     * - TTL 10분: 이벤트 정보는 짧은 주기로 변경되지 않음
     */
    @Cacheable(value = "notices", key = "'events'")
    public List<EventResponse> getEvents() {
        log.debug("이벤트 목록 조회 - API 호출");
        EventResponse[] response = lostarkApiClient.get(
                uriBuilder -> uriBuilder.path("/news/events").build(),
                EventResponse[].class
        );
        return response != null ? Arrays.asList(response) : List.of();
    }
}
