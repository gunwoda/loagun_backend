package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 아크패시브 조회
 * GET /armories/characters/{characterName}/arkpassive
 */
@Getter
@NoArgsConstructor
public class ArkPassiveResponse {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("IsArkPassive")
    private Boolean isArkPassive;

    @JsonProperty("Points")
    private List<Point> points;

    @JsonProperty("Effects")
    private List<Effect> effects;

    @Getter
    @NoArgsConstructor
    public static class Point {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private Integer value;

        @JsonProperty("Tooltip")
        private String tooltip;

        @JsonProperty("Description")
        private String description;
    }

    @Getter
    @NoArgsConstructor
    public static class Effect {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Description")
        private String description;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("ToolTip")
        private String toolTip;
    }
}
