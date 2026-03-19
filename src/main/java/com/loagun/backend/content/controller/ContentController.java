package com.loagun.backend.content.controller;

import com.loagun.backend.content.dto.ContentsCalendarResponse;
import com.loagun.backend.content.service.ContentService;
import com.loagun.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Content", description = "게임 컨텐츠 정보 조회 API")
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @Operation(
            summary = "이번 주 컨텐츠 캘린더 조회",
            description = """
                    이번 주 운영 중인 게임 컨텐츠 스케줄과 보상 정보를 조회합니다.
                    - 카오스게이트, 필드보스, 모험 섬, 군단장 레이드 등 포함
                    - 각 컨텐츠별 시작 시간, 최소 아이템레벨, 보상 아이템 제공
                    - 1시간 캐싱 적용 (주간 리셋 기반으로 빈번히 변경되지 않음)
                    """
    )
    @GetMapping("/calendar")
    public ApiResponse<List<ContentsCalendarResponse>> getCalendar() {
        return ApiResponse.ok(contentService.getCalendar());
    }
}
