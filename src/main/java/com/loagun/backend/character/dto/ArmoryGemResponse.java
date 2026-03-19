package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 보석 조회
 * GET /armories/characters/{characterName}/gems
 */
@Getter
@NoArgsConstructor
public class ArmoryGemResponse {

    @JsonProperty("Gems")
    private List<Gem> gems;

    @JsonProperty("Effects")
    private GemEffects effects;

    @Getter
    @NoArgsConstructor
    public static class Gem {
        @JsonProperty("Slot")
        private Integer slot;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("Level")
        private Integer level;

        @JsonProperty("Grade")
        private String grade;

        @JsonProperty("Tooltip")
        private String tooltip;
    }

    @Getter
    @NoArgsConstructor
    public static class GemEffects {
        @JsonProperty("Description")
        private String description;

        @JsonProperty("Skills")
        private List<GemSkillEffect> skills;
    }

    @Getter
    @NoArgsConstructor
    public static class GemSkillEffect {
        @JsonProperty("GemSlot")
        private Integer gemSlot;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Description")
        private List<String> description;

        @JsonProperty("Option")
        private String option;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("Tooltip")
        private String tooltip;
    }
}
