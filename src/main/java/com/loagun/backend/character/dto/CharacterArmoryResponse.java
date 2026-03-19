package com.loagun.backend.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 캐릭터 전체 정보 통합 응답
 * GET /armories/characters/{characterName}?filters=profiles+equipment+avatars+combat-skills+engravings+cards+gems+collectibles+arkpassive
 *
 * 단일 API 호출로 모든 섹션을 가져와 불필요한 외부 API 호출을 최소화
 */
@Getter
@NoArgsConstructor
public class CharacterArmoryResponse {

    @JsonProperty("ArmoryProfile")
    private CharacterProfileResponse profile;

    @JsonProperty("ArmoryEquipment")
    private List<ArmoryEquipmentResponse> equipment;

    @JsonProperty("ArmoryAvatars")
    private List<ArmoryAvatarResponse> avatars;

    @JsonProperty("ArmorySkills")
    private List<ArmorySkillResponse> skills;

    @JsonProperty("ArmoryEngravings")
    private ArmoryEngravingResponse engravings;

    @JsonProperty("ArmoryCards")
    private ArmoryCardResponse cards;

    @JsonProperty("ArmoryGems")
    private ArmoryGemResponse gems;

    @JsonProperty("Collectibles")
    private List<CollectibleResponse> collectibles;

    @JsonProperty("ArkPassive")
    private ArkPassiveResponse arkPassive;
}
