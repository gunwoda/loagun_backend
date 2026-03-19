package com.loagun.backend.character.controller;

import com.loagun.backend.character.dto.*;
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
            summary = "캐릭터 전체 정보 조회 (통합)",
            description = """
                    캐릭터의 모든 정보를 한 번에 조회합니다.
                    - 프로필 (전투레벨, 아이템레벨, 원정대레벨, 스탯 등)
                    - 장비 (무기, 방어구, 장신구 + 툴팁)
                    - 아바타
                    - 전투 스킬 (트라이포드, 룬)
                    - 각인 (각인 슬롯, 활성화 각인 효과)
                    - 카드 (카드 슬롯, 세트 효과)
                    - 보석 (보석 슬롯, 스킬 효과)
                    - 수집형 포인트 (섬의 마음, 모코코, 위대한 항해 등)
                    - 아크패시브

                    단일 API 호출로 모든 정보를 수신하며, 5분 캐싱이 적용됩니다.
                    """
    )
    @GetMapping("/{characterName}/armory")
    public ApiResponse<CharacterArmoryResponse> getArmory(
            @Parameter(description = "캐릭터명", example = "깜지직") @PathVariable String characterName
    ) {
        return ApiResponse.ok(characterService.getArmory(characterName));
    }

    @Operation(
            summary = "캐릭터 프로필 조회",
            description = "캐릭터 기본 프로필 정보만 조회합니다. (전투레벨, 아이템레벨, 클래스, 원정대레벨 등)"
    )
    @GetMapping("/{characterName}/profile")
    public ApiResponse<CharacterProfileResponse> getProfile(
            @Parameter(description = "캐릭터명", example = "깜지직") @PathVariable String characterName
    ) {
        return ApiResponse.ok(characterService.getProfile(characterName));
    }

    @Operation(
            summary = "원정대 캐릭터 목록 조회",
            description = "동일 계정의 보유 캐릭터 전체 목록을 조회합니다."
    )
    @GetMapping("/{characterName}/siblings")
    public ApiResponse<List<CharacterSiblingResponse>> getSiblings(
            @Parameter(description = "캐릭터명 (계정 내 아무 캐릭터명)", example = "깜지직") @PathVariable String characterName
    ) {
        return ApiResponse.ok(characterService.getSiblings(characterName));
    }
}
