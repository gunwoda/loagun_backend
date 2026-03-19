package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로스트아크 Open API - 계정 내 보유 캐릭터 목록 응답
 * GET /characters/{characterName}/siblings
 */
@Getter
@NoArgsConstructor
public class CharacterSiblingResponse {

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
}
