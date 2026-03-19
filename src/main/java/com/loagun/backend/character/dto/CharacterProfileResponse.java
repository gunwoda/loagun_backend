package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 캐릭터 프로필 응답
 * GET /armories/characters/{characterName}/profiles
 */
@Getter
@NoArgsConstructor
public class CharacterProfileResponse {

    @JsonProperty("CharacterImage")
    private String characterImage;

    @JsonProperty("ExpeditionLevel")
    private Integer expeditionLevel;

    @JsonProperty("PvpGrade")
    private String pvpGrade;

    @JsonProperty("TownLevel")
    private Integer townLevel;

    @JsonProperty("TownName")
    private String townName;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("GuildMemberGrade")
    private String guildMemberGrade;

    @JsonProperty("GuildName")
    private String guildName;

    @JsonProperty("Stats")
    private List<Stat> stats;

    @JsonProperty("Tendencies")
    private List<Tendency> tendencies;

    @JsonProperty("ServerName")
    private String serverName;

    @JsonProperty("CharacterName")
    private String characterName;

    @JsonProperty("CharacterLevel")
    private Integer characterLevel;

    @JsonProperty("CharacterClassName")
    private String characterClassName;

    @JsonProperty("ItemAvgLevel")
    private String itemAvgLevel;

    @JsonProperty("ItemMaxLevel")
    private String itemMaxLevel;

    @Getter
    @NoArgsConstructor
    public static class Stat {
        @JsonProperty("Type")
        private String type;

        @JsonProperty("Value")
        private String value;

        @JsonProperty("Tooltip")
        private List<String> tooltip;
    }

    @Getter
    @NoArgsConstructor
    public static class Tendency {
        @JsonProperty("Type")
        private String type;

        @JsonProperty("Point")
        private Integer point;

        @JsonProperty("MaxPoint")
        private Integer maxPoint;
    }
}
