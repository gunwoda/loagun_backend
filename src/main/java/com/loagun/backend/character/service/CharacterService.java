package com.loagun.backend.character.service;

import com.loagun.backend.character.dto.CharacterProfileResponse;
import com.loagun.backend.character.dto.CharacterSiblingResponse;
import com.loagun.backend.global.client.LostarkApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterService {

    private final LostarkApiClient lostarkApiClient;

    /**
     * 캐릭터 프로필 조회
     * - Redis 캐시 TTL: 5분 (RedisConfig 참고)
     * - 동일 캐릭터 반복 조회 시 로스트아크 API rate limit 방어
     */
    @Cacheable(value = "character", key = "'profile:' + #characterName")
    public CharacterProfileResponse getProfile(String characterName) {
        log.debug("캐릭터 프로필 조회 - API 호출: {}", characterName);
        String path = "/armories/characters/" + encode(characterName) + "/profiles";
        return lostarkApiClient.get(path, CharacterProfileResponse.class);
    }

    /**
     * 계정 내 보유 캐릭터 목록 조회 (원정대)
     * - Redis 캐시 TTL: 5분
     */
    @Cacheable(value = "character", key = "'siblings:' + #characterName")
    public List<CharacterSiblingResponse> getSiblings(String characterName) {
        log.debug("보유 캐릭터 목록 조회 - API 호출: {}", characterName);
        String path = "/characters/" + encode(characterName) + "/siblings";
        CharacterSiblingResponse[] response = lostarkApiClient.get(path, CharacterSiblingResponse[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    private String encode(String characterName) {
        return java.net.URLEncoder.encode(characterName, java.nio.charset.StandardCharsets.UTF_8);
    }
}
