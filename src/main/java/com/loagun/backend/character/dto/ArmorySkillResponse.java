package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 전투 스킬 조회
 * GET /armories/characters/{characterName}/combat-skills
 */
@Getter
@NoArgsConstructor
public class ArmorySkillResponse {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Icon")
    private String icon;

    @JsonProperty("Level")
    private Integer level;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("SkillType")
    private Integer skillType;

    @JsonProperty("Tripods")
    private List<Tripod> tripods;

    @JsonProperty("Rune")
    private Rune rune;

    @JsonProperty("Tooltip")
    private String tooltip;

    @Getter
    @NoArgsConstructor
    public static class Tripod {
        @JsonProperty("Tier")
        private Integer tier;

        @JsonProperty("Slot")
        private Integer slot;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("IsSelected")
        private Boolean isSelected;

        @JsonProperty("Tooltip")
        private String tooltip;
    }

    @Getter
    @NoArgsConstructor
    public static class Rune {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("Grade")
        private String grade;

        @JsonProperty("Tooltip")
        private String tooltip;
    }
}
