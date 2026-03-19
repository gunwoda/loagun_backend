package com.loagun.backend.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 이번 주 컨텐츠 캘린더
 * GET /gamecontents/calendar
 *
 * 카오스게이트, 필드보스, 모험 섬, 군단장 레이드 등
 * 이번 주 운영 컨텐츠의 스케줄과 보상 정보를 포함
 */
@Getter
@NoArgsConstructor
public class ContentsCalendarResponse {

    @JsonProperty("CategoryName")
    private String categoryName;

    @JsonProperty("ContentsName")
    private String contentsName;

    @JsonProperty("ContentsIcon")
    private String contentsIcon;

    @JsonProperty("MinItemLevel")
    private Integer minItemLevel;

    @JsonProperty("StartTimes")
    private List<String> startTimes;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("RewardItems")
    private List<LevelRewardItems> rewardItems;

    @Getter
    @NoArgsConstructor
    public static class LevelRewardItems {

        @JsonProperty("ItemLevel")
        private Integer itemLevel;

        @JsonProperty("Items")
        private List<RewardItem> items;
    }

    @Getter
    @NoArgsConstructor
    public static class RewardItem {

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("Grade")
        private String grade;

        @JsonProperty("StartTimes")
        private List<String> startTimes;
    }
}
