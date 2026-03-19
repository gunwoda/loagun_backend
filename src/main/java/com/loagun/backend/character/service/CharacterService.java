package com.loagun.backend.character.service;

import com.loagun.backend.character.dto.*;
import com.loagun.backend.global.client.LostarkApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {

    private final LostarkApiClient lostarkApiClient;

    // 전체 armory 조회에 사용할 필터 (단일 API 호출로 모든 정보 수신)
    private static final String ARMORY_FILTERS =
            "profiles+equipment+avatars+combat-skills+engravings+cards+gems+collectibles+arkpassive";

    /**
     * 캐릭터 전체 정보 통합 조회
     * - 단일 API 호출로 프로필, 장비, 스킬, 각인, 카드, 보석, 수집형, 아크패시브 전부 수신
     * - API Rate Limit 절약: 개별 엔드포인트 8번 → 1번으로 감소
     */
    @Cacheable(value = "character", key = "'armory:' + #characterName")
    public CharacterArmoryResponse getArmory(String characterName) {
        log.debug("캐릭터 전체 정보 조회 - API 호출: {}", characterName);
        String path = "/armories/characters/" + encode(characterName) + "?filters=" + ARMORY_FILTERS;
        return lostarkApiClient.get(path, CharacterArmoryResponse.class);
    }

    /**
     * 캐릭터 프로필 조회 (경량)
     */
    @Cacheable(value = "character", key = "'profile:' + #characterName")
    public CharacterProfileResponse getProfile(String characterName) {
        log.debug("캐릭터 프로필 조회 - API 호출: {}", characterName);
        String path = "/armories/characters/" + encode(characterName) + "/profiles";
        return lostarkApiClient.get(path, CharacterProfileResponse.class);
    }

    /**
     * 계정 내 보유 캐릭터 목록 조회 (원정대)
     */
    @Cacheable(value = "character", key = "'siblings:' + #characterName")
    public List<CharacterSiblingResponse> getSiblings(String characterName) {
        log.debug("보유 캐릭터 목록 조회 - API 호출: {}", characterName);
        String path = "/characters/" + encode(characterName) + "/siblings";
        CharacterSiblingResponse[] response = lostarkApiClient.get(path, CharacterSiblingResponse[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    private String encode(String characterName) {
        return URLEncoder.encode(characterName, StandardCharsets.UTF_8);
    }
}
