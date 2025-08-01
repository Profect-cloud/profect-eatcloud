package profect.eatcloud.Domain.Store.Dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalesStatisticsDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailySales {
        private LocalDate date;
        private Integer orderCount;
        private BigDecimal totalAmount;
        private BigDecimal avgOrderAmount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodSummary {
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer totalOrderCount;
        private BigDecimal totalAmount;
        private BigDecimal avgDailyAmount;
        private BigDecimal avgOrderAmount;
        private BigDecimal maxDailyAmount;
        private BigDecimal minDailyAmount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesResponse {
        private PeriodSummary summary;
        private List<DailySales> dailySales;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopMenuSales {
        private String menuName;
        private Integer totalQuantity;
        private BigDecimal totalAmount;
        private BigDecimal avgPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuSalesResponse {
        private List<TopMenuSales> topMenus;
        private Integer totalMenuCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonSales {
        private PeriodSummary currentPeriod;
        private PeriodSummary previousPeriod;
        private BigDecimal growthRate;
        private String growthType;
    }
}
