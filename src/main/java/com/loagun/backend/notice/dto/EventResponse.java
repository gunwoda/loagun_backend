package com.loagun.backend.notice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로스트아크 Open API - 이벤트 조회
 * GET /news/events
 */
@Getter
@NoArgsConstructor
public class EventResponse {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Thumbnail")
    private String thumbnail;

    @JsonProperty("Link")
    private String link;

    @JsonProperty("StartDate")
    private String startDate;

    @JsonProperty("EndDate")
    private String endDate;

    @JsonProperty("RewardDate")
    private String rewardDate;
}
