package com.loagun.backend.content.service;

import com.loagun.backend.content.dto.ContentsCalendarResponse;
import com.loagun.backend.global.client.LostarkApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final LostarkApiClient lostarkApiClient;

    /**
     * 이번 주 컨텐츠 캘린더 조회
     * - 카오스게이트, 필드보스, 모험 섬, 군단장 레이드 등 스케줄 + 보상 포함
     * - TTL 1시간: 캘린더는 주간 리셋 기반으로 빈번히 변경되지 않음
     */
    @Cacheable(value = "contents", key = "'calendar'")
    public List<ContentsCalendarResponse> getCalendar() {
        log.debug("컨텐츠 캘린더 조회 - API 호출");
        ContentsCalendarResponse[] response = lostarkApiClient.get(
                uriBuilder -> uriBuilder.path("/gamecontents/calendar").build(),
                ContentsCalendarResponse[].class
        );
        return response != null ? Arrays.asList(response) : List.of();
    }
}
