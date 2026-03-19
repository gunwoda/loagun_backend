package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 각인 조회
 * GET /armories/characters/{characterName}/engravings
 */
@Getter
@NoArgsConstructor
public class ArmoryEngravingResponse {

    @JsonProperty("Engravings")
    private List<Engraving> engravings;

    @JsonProperty("Effects")
    private List<Effect> effects;

    @JsonProperty("ArkPassiveEffects")
    private List<ArkPassiveEffect> arkPassiveEffects;

    @Getter
    @NoArgsConstructor
    public static class Engraving {
        @JsonProperty("Slot")
        private Integer slot;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("Tooltip")
        private String tooltip;
    }

    @Getter
    @NoArgsConstructor
    public static class Effect {
        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Description")
        private String description;
    }

    @Getter
    @NoArgsConstructor
    public static class ArkPassiveEffect {
        @JsonProperty("AbilityStoneLevel")
        private Integer abilityStoneLevel;

        @JsonProperty("Grade")
        private String grade;

        @JsonProperty("Level")
        private Integer level;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Description")
        private String description;
    }
}
