package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 카드 조회
 * GET /armories/characters/{characterName}/cards
 */
@Getter
@NoArgsConstructor
public class ArmoryCardResponse {

    @JsonProperty("Cards")
    private List<Card> cards;

    @JsonProperty("Effects")
    private List<CardEffect> effects;

    @Getter
    @NoArgsConstructor
    public static class Card {
        @JsonProperty("Slot")
        private Integer slot;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("AwakeCount")
        private Integer awakeCount;

        @JsonProperty("AwakeTotal")
        private Integer awakeTotal;

        @JsonProperty("Grade")
        private String grade;

        @JsonProperty("Tooltip")
        private String tooltip;
    }

    @Getter
    @NoArgsConstructor
    public static class CardEffect {
        @JsonProperty("Index")
        private Integer index;

        @JsonProperty("CardSlots")
        private List<Integer> cardSlots;

        @JsonProperty("Items")
        private List<EffectItem> items;

        @Getter
        @NoArgsConstructor
        public static class EffectItem {
            @JsonProperty("Name")
            private String name;

            @JsonProperty("Description")
            private String description;
        }
    }
}
