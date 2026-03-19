package com.loagun.backend.character.controller;

import com.loagun.backend.character.dto.CharacterProfileResponse;
import com.loagun.backend.character.dto.CharacterSiblingResponse;
import com.loagun.backend.character.service.CharacterService;
import com.loagun.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Character", description = "캐릭터 정보 조회 API")
@RestController
@RequestMapping("/api/v1/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @Operation(
            summary = "캐릭터 프로필 조회",
            description = "캐릭터명으로 기본 프로필 정보(전투 레벨, 아이템 레벨, 클래스 등)를 조회합니다. 5분 캐싱 적용"
    )
    @GetMapping("/{characterName}/profile")
    public ApiResponse<CharacterProfileResponse> getProfile(
            @Parameter(description = "캐릭터명", example = "아르떼미스") @PathVariable String characterName
    ) {
        return ApiResponse.ok(characterService.getProfile(characterName));
    }

    @Operation(
            summary = "원정대 캐릭터 목록 조회",
            description = "동일 계정의 보유 캐릭터 전체 목록을 조회합니다. 5분 캐싱 적용"
    )
    @GetMapping("/{characterName}/siblings")
    public ApiResponse<List<CharacterSiblingResponse>> getSiblings(
            @Parameter(description = "캐릭터명 (계정 내 아무 캐릭터명)", example = "아르떼미스") @PathVariable String characterName
    ) {
        return ApiResponse.ok(characterService.getSiblings(characterName));
    }
}
