package com.loagun.backend.auction.controller;

import com.loagun.backend.auction.dto.AuctionOptionResponse;
import com.loagun.backend.auction.dto.AuctionResponse;
import com.loagun.backend.auction.dto.AuctionSearchRequest;
import com.loagun.backend.auction.service.AuctionService;
import com.loagun.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auction", description = "경매장 조회 API")
@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @Operation(
            summary = "경매장 검색 옵션 조회",
            description = """
                    경매장 검색에 필요한 옵션 메타데이터를 조회합니다.
                    - 카테고리 코드 목록 (검색 시 CategoryCode에 사용)
                    - 직업 목록, 아이템 등급, 티어 목록
                    - 스킬 옵션, 기타 옵션 (각인, 전투 특성 등)
                    - 1시간 캐싱 적용 (패치 전까지 변경 없음)
                    """
    )
    @GetMapping("/options")
    public ApiResponse<AuctionOptionResponse> getOptions() {
        return ApiResponse.ok(auctionService.getOptions());
    }

    @Operation(
            summary = "경매장 아이템 검색",
            description = """
                    조건에 맞는 경매장 아이템 목록을 조회합니다.
                    - 카테고리, 아이템명, 등급, 티어, 아이템레벨, 품질로 필터링
                    - 스킬 옵션 / 기타 옵션 (각인, 전투 특성 등) 상세 조건 가능
                    - 정렬: BUY_PRICE(즉시구매가), BIDSTART_PRICE(입찰가), EXPIREDATE(마감임박) 등
                    - 1분 캐싱 적용 (실시간 가격 변동 반영)
                    """
    )
    @PostMapping("/items")
    public ApiResponse<AuctionResponse> searchItems(@RequestBody @Valid AuctionSearchRequest request) {
        return ApiResponse.ok(auctionService.searchItems(request));
    }
}
