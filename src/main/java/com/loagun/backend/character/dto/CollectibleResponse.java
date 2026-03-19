package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 수집형 포인트 조회
 * GET /armories/characters/{characterName}/collectibles
 */
@Getter
@NoArgsConstructor
public class CollectibleResponse {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Icon")
    private String icon;

    @JsonProperty("Point")
    private Integer point;

    @JsonProperty("MaxPoint")
    private Integer maxPoint;

    @JsonProperty("CollectiblePoints")
    private List<CollectiblePoint> collectiblePoints;

    @Getter
    @NoArgsConstructor
    public static class CollectiblePoint {
        @JsonProperty("PointName")
        private String pointName;

        @JsonProperty("Point")
        private Integer point;

        @JsonProperty("MaxPoint")
        private Integer maxPoint;
    }
}
