package com.loagun.backend.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로스트아크 Open API - 공지사항 조회
 * GET /news/notices
 */
@Getter
@NoArgsConstructor
public class NoticeResponse {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Date")
    private String date;

    @JsonProperty("Link")
    private String link;

    @JsonProperty("Type")
    private String type;
}
