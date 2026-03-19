package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로스트아크 Open API - 아바타 조회
 * GET /armories/characters/{characterName}/avatars
 */
@Getter
@NoArgsConstructor
public class ArmoryAvatarResponse {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Icon")
    private String icon;

    @JsonProperty("Grade")
    private String grade;

    @JsonProperty("IsSet")
    private Boolean isSet;

    @JsonProperty("IsInner")
    private Boolean isInner;

    @JsonProperty("Tooltip")
    private String tooltip;
}
