package com.loagun.backend.auction.service;

import com.loagun.backend.auction.dto.AuctionOptionResponse;
import com.loagun.backend.auction.dto.AuctionResponse;
import com.loagun.backend.auction.dto.AuctionSearchRequest;
import com.loagun.backend.global.client.LostarkApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionService {

    private final LostarkApiClient lostarkApiClient;

    /**
     * 경매장 검색 옵션 조회 (카테고리 코드, 직업, 등급 등)
     * - 프론트에서 검색 필터 UI 구성에 사용
     * - TTL 1시간: 옵션 메타데이터는 패치 전까지 변경되지 않음
     */
    @Cacheable(value = "contents", key = "'auction-options'")
    public AuctionOptionResponse getOptions() {
        log.debug("경매장 검색 옵션 조회 - API 호출");
        return lostarkApiClient.get(
                uriBuilder -> uriBuilder.path("/auctions/options").build(),
                AuctionOptionResponse.class
        );
    }

    /**
     * 경매장 아이템 검색
     * - TTL 1분: 실시간 가격 변동이 잦으므로 짧은 캐싱 적용
     * - 캐시 키: 검색 조건의 주요 필드 조합
     */
    @Cacheable(
            value = "auction",
            key = "'items:' + #request.categoryCode + ':' + #request.itemName + ':' + #request.itemGrade + ':' + #request.itemTier + ':' + #request.sort + ':' + #request.sortCondition + ':' + #request.pageNo"
    )
    public AuctionResponse searchItems(AuctionSearchRequest request) {
        log.debug("경매장 아이템 검색 - API 호출: category={}, name={}", request.getCategoryCode(), request.getItemName());
        return lostarkApiClient.post(
                "/auctions/items",
                request,
                AuctionResponse.class,
                Map.of()
        );
    }
}
