package com.loagun.backend.notice.controller;

import com.loagun.backend.global.common.response.ApiResponse;
import com.loagun.backend.notice.dto.EventResponse;
import com.loagun.backend.notice.dto.NoticeResponse;
import com.loagun.backend.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Notice", description = "공지사항 및 이벤트 조회 API")
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(
            summary = "공지사항 조회",
            description = """
                    로스트아크 공지사항 목록을 조회합니다. 10분 캐싱 적용.

                    **type 필터:**
                    - `공지`: 일반 공지
                    - `점검`: 서버 점검 안내
                    - `상점`: 상점 업데이트 안내
                    - `이벤트`: 이벤트 안내
                    - 미입력 시 전체 조회
                    """
    )
    @GetMapping("/notices")
    public ApiResponse<List<NoticeResponse>> getNotices(
            @Parameter(description = "공지 유형 (공지 | 점검 | 상점 | 이벤트)")
            @RequestParam(required = false) String type,
            @Parameter(description = "제목 검색 키워드")
            @RequestParam(required = false) String searchText
    ) {
        return ApiResponse.ok(noticeService.getNotices(type, searchText));
    }

    @Operation(
            summary = "진행 중인 이벤트 조회",
            description = "현재 진행 중인 이벤트 목록을 조회합니다. 10분 캐싱 적용."
    )
    @GetMapping("/events")
    public ApiResponse<List<EventResponse>> getEvents() {
        return ApiResponse.ok(noticeService.getEvents());
    }
}
