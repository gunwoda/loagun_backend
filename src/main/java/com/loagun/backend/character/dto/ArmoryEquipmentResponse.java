package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로스트아크 Open API - 장비 조회
 * GET /armories/characters/{characterName}/equipment
 * Tooltip은 복잡한 JSON 문자열로 프론트에서 파싱
 */
@Getter
@NoArgsConstructor
public class ArmoryEquipmentResponse {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Icon")
    private String icon;

    @JsonProperty("Grade")
    private String grade;

    @JsonProperty("Tooltip")
    private String tooltip;
}
