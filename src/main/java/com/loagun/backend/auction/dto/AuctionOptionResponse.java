package com.loagun.backend.auction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 로스트아크 Open API - 경매장 검색 옵션
 * GET /auctions/options
 * 프론트에서 검색 UI를 구성할 때 사용 (카테고리 코드 목록 등)
 */
@Getter
@NoArgsConstructor
public class AuctionOptionResponse {

    @JsonProperty("MaxItemLevel")
    private Integer maxItemLevel;

    @JsonProperty("ItemGradeQualities")
    private List<Integer> itemGradeQualities;

    @JsonProperty("SkillOptions")
    private List<SkillOption> skillOptions;

    @JsonProperty("EtcOptions")
    private List<EtcOption> etcOptions;

    @JsonProperty("Categories")
    private List<Category> categories;

    @JsonProperty("ItemGrades")
    private List<String> itemGrades;

    @JsonProperty("ItemTiers")
    private List<Integer> itemTiers;

    @JsonProperty("Classes")
    private List<String> classes;

    @Getter
    @NoArgsConstructor
    public static class Category {
        @JsonProperty("Subs")
        private List<CategorySub> subs;

        @JsonProperty("Code")
        private Integer code;

        @JsonProperty("CodeName")
        private String codeName;

        @Getter
        @NoArgsConstructor
        public static class CategorySub {
            @JsonProperty("Code")
            private Integer code;

            @JsonProperty("CodeName")
            private String codeName;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SkillOption {
        @JsonProperty("Value")
        private Integer value;

        @JsonProperty("Class")
        private String className;

        @JsonProperty("Text")
        private String text;

        @JsonProperty("Tripods")
        private List<Tripod> tripods;

        @Getter
        @NoArgsConstructor
        public static class Tripod {
            @JsonProperty("Value")
            private Integer value;

            @JsonProperty("Text")
            private String text;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class EtcOption {
        @JsonProperty("Value")
        private Integer value;

        @JsonProperty("Text")
        private String text;

        @JsonProperty("EtcSubs")
        private List<EtcSub> etcSubs;

        @Getter
        @NoArgsConstructor
        public static class EtcSub {
            @JsonProperty("Value")
            private Integer value;

            @JsonProperty("Text")
            private String text;
        }
    }
}
