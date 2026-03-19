package com.loagun.backend.auction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 경매장 아이템 검색 응답
 * POST /auctions/items
 */
@Getter
@NoArgsConstructor
public class AuctionResponse {

    @JsonProperty("PageNo")
    private Integer pageNo;

    @JsonProperty("PageSize")
    private Integer pageSize;

    @JsonProperty("TotalCount")
    private Integer totalCount;

    @JsonProperty("Items")
    private List<AuctionItem> items;

    @Getter
    @NoArgsConstructor
    public static class AuctionItem {

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Grade")
        private String grade;

        @JsonProperty("Tier")
        private Integer tier;

        @JsonProperty("Level")
        private Integer level;

        @JsonProperty("Icon")
        private String icon;

        @JsonProperty("GradeQuality")
        private Integer gradeQuality;

        @JsonProperty("AuctionInfo")
        private AuctionInfo auctionInfo;

        @JsonProperty("Options")
        private List<ItemOption> options;
    }

    @Getter
    @NoArgsConstructor
    public static class AuctionInfo {

        @JsonProperty("StartPrice")
        private Long startPrice;

        @JsonProperty("BuyPrice")
        private Long buyPrice;

        @JsonProperty("BidPrice")
        private Long bidPrice;

        @JsonProperty("EndDate")
        private String endDate;

        @JsonProperty("BidCount")
        private Integer bidCount;

        @JsonProperty("BidStartPrice")
        private Long bidStartPrice;

        @JsonProperty("IsCompetitive")
        private Boolean isCompetitive;

        @JsonProperty("TradeAllowCount")
        private Integer tradeAllowCount;
    }

    @Getter
    @NoArgsConstructor
    public static class ItemOption {

        @JsonProperty("Type")
        private String type;

        @JsonProperty("OptionName")
        private String optionName;

        @JsonProperty("OptionNameTripod")
        private String optionNameTripod;

        @JsonProperty("Value")
        private Double value;

        @JsonProperty("IsPenalty")
        private Boolean isPenalty;

        @JsonProperty("ClassName")
        private String className;

        @JsonProperty("IsValuePercentage")
        private Boolean isValuePercentage;
    }
}
