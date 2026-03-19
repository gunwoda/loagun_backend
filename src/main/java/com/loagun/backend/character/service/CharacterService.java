package com.loagun.backend.character.service;

import com.loagun.backend.character.dto.*;
import com.loagun.backend.global.client.LostarkApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {

    private final LostarkApiClient lostarkApiClient;

    private static final String ARMORY_FILTERS =
            "profiles+equipment+avatars+combat-skills+engravings+cards+gems+collectibles+arkpassive";

    @Cacheable(value = "character", key = "'armory:' + #characterName")
    public CharacterArmoryResponse getArmory(String characterName) {
        log.debug("캐릭터 전체 정보 조회 - API 호출: {}", characterName);
        return lostarkApiClient.get(
                "/armories/characters/{name}?filters=" + ARMORY_FILTERS,
                CharacterArmoryResponse.class,
                Map.of("name", characterName)
        );
    }

    @Cacheable(value = "character", key = "'profile:' + #characterName")
    public CharacterProfileResponse getProfile(String characterName) {
        log.debug("캐릭터 프로필 조회 - API 호출: {}", characterName);
        return lostarkApiClient.get(
                "/armories/characters/{name}/profiles",
                CharacterProfileResponse.class,
                Map.of("name", characterName)
        );
    }

    @Cacheable(value = "character", key = "'siblings:' + #characterName")
    public List<CharacterSiblingResponse> getSiblings(String characterName) {
        log.debug("보유 캐릭터 목록 조회 - API 호출: {}", characterName);
        CharacterSiblingResponse[] response = lostarkApiClient.get(
                "/characters/{name}/siblings",
                CharacterSiblingResponse[].class,
                Map.of("name", characterName)
        );
        return response != null ? Arrays.asList(response) : List.of();
    }

    /**
     * 특정 캐릭터의 캐시 전체 삭제
     * 캐릭터 정보가 갱신되었을 때 클라이언트가 명시적으로 호출
     */
    @CacheEvict(value = "character", allEntries = false, key = "'armory:' + #characterName")
    public void evictArmoryCache(String characterName) {
        log.info("캐릭터 armory 캐시 삭제: {}", characterName);
    }

    @CacheEvict(value = "character", allEntries = false, key = "'profile:' + #characterName")
    public void evictProfileCache(String characterName) {
        log.info("캐릭터 profile 캐시 삭제: {}", characterName);
    }

    @CacheEvict(value = "character", allEntries = false, key = "'siblings:' + #characterName")
    public void evictSiblingsCache(String characterName) {
        log.info("캐릭터 siblings 캐시 삭제: {}", characterName);
    }
}
