package com.loagun.backend.auction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "경매장 아이템 검색 요청")
public class AuctionSearchRequest {

    @Schema(description = "카테고리 코드 (auctions/options에서 조회)", example = "180010")
    @JsonProperty("CategoryCode")
    private Integer categoryCode;

    @Schema(description = "아이템 이름", example = "카양겔")
    @JsonProperty("ItemName")
    private String itemName;

    @Schema(description = "직업", example = "버서커")
    @JsonProperty("CharacterClass")
    private String characterClass;

    @Schema(description = "아이템 등급", example = "유물")
    @JsonProperty("ItemGrade")
    private String itemGrade;

    @Schema(description = "아이템 티어 (3 or 4)", example = "4")
    @JsonProperty("ItemTier")
    private Integer itemTier;

    @Schema(description = "최소 아이템 레벨", example = "1600")
    @JsonProperty("ItemLevelMin")
    private Integer itemLevelMin;

    @Schema(description = "최대 아이템 레벨", example = "1700")
    @JsonProperty("ItemLevelMax")
    private Integer itemLevelMax;

    @Schema(description = "아이템 최소 품질 (0~100)", example = "90")
    @Min(0) @Max(100)
    @JsonProperty("ItemGradeQuality")
    private Integer itemGradeQuality;

    @Schema(description = "정렬 기준", example = "BUY_PRICE",
            allowableValues = {"BIDSTART_PRICE", "BUY_PRICE", "EXPIREDATE", "ITEM_GRADE", "ITEM_LEVEL", "ITEM_QUALITY"})
    @JsonProperty("Sort")
    private String sort = "BUY_PRICE";

    @Schema(description = "정렬 순서", example = "ASC", allowableValues = {"ASC", "DESC"})
    @JsonProperty("SortCondition")
    private String sortCondition = "ASC";

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    @Min(1)
    @JsonProperty("PageNo")
    private Integer pageNo = 1;

    @JsonProperty("SkillOptions")
    private List<SearchOption> skillOptions;

    @JsonProperty("EtcOptions")
    private List<SearchOption> etcOptions;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchOption {
        @JsonProperty("FirstOption")
        private Integer firstOption;

        @JsonProperty("SecondOption")
        private Integer secondOption;

        @JsonProperty("MinValue")
        private Integer minValue;

        @JsonProperty("MaxValue")
        private Integer maxValue;
    }
}
