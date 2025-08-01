package profect.eatcloud.Domain.Store.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Store.Dto.SalesStatisticsDto;
import profect.eatcloud.Domain.Store.Entity.DailyStoreSales;
import profect.eatcloud.Domain.Store.Exception.SalesStatisticsException;
import profect.eatcloud.Domain.Store.Repository.MenuSalesRepository;
import profect.eatcloud.Domain.Store.Repository.SalesStatisticsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SalesStatisticsService {

    private final SalesStatisticsRepository salesStatisticsRepository;
    private final MenuSalesRepository menuSalesRepository;

    public SalesStatisticsDto.SalesResponse getSalesStatistics(UUID storeId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        validateStoreId(storeId);
        
        List<DailyStoreSales> dailySalesList = salesStatisticsRepository
                .findByStoreIdAndDateRange(storeId, startDate, endDate);

        Object[] summaryData = salesStatisticsRepository
                .findSalesSummaryByStoreIdAndDateRange(storeId, startDate, endDate);

        List<SalesStatisticsDto.DailySales> dailySales = dailySalesList.stream()
                .map(this::convertToDailySalesDto)
                .collect(Collectors.toList());

        SalesStatisticsDto.PeriodSummary summary = convertToPeriodSummaryDto(
                summaryData, startDate, endDate
        );

        return SalesStatisticsDto.SalesResponse.builder()
                .summary(summary)
                .dailySales(dailySales)
                .build();
    }

    public SalesStatisticsDto.SalesResponse getRecentSalesStatistics(UUID storeId, int days) {
        LocalDate endDate = LocalDate.now().minusDays(1); // 어제까지
        LocalDate startDate = endDate.minusDays(days - 1);
        
        return getSalesStatistics(storeId, startDate, endDate);
    }

    public SalesStatisticsDto.MenuSalesResponse getMenuSalesStatistics(
            UUID storeId, LocalDate startDate, LocalDate endDate, Integer limit) {
        
        List<Object[]> topMenusData = menuSalesRepository
                .findTopMenuSalesByStoreIdAndDateRange(storeId, startDate, endDate);

        if (limit != null && limit > 0) {
            topMenusData = topMenusData.stream()
                    .limit(limit)
                    .toList();
        }

        List<SalesStatisticsDto.TopMenuSales> topMenus = topMenusData.stream()
                .map(this::convertToTopMenuSalesDto)
                .collect(Collectors.toList());

        Long totalMenuCount = menuSalesRepository
                .countDistinctMenusByStoreIdAndDateRange(storeId, startDate, endDate);

        return SalesStatisticsDto.MenuSalesResponse.builder()
                .topMenus(topMenus)
                .totalMenuCount(totalMenuCount.intValue())
                .build();
    }

    public SalesStatisticsDto.ComparisonSales getComparisonSalesStatistics(
            UUID storeId, LocalDate currentStart, LocalDate currentEnd) {
        
        Object[] currentData = salesStatisticsRepository
                .findSalesSummaryByStoreIdAndDateRange(storeId, currentStart, currentEnd);
        
        long periodLength = currentEnd.toEpochDay() - currentStart.toEpochDay() + 1;
        LocalDate previousEnd = currentStart.minusDays(1);
        LocalDate previousStart = previousEnd.minusDays(periodLength - 1);
        
        Object[] previousData = salesStatisticsRepository
                .findSalesSummaryByStoreIdAndDateRange(storeId, previousStart, previousEnd);

        SalesStatisticsDto.PeriodSummary currentSummary = convertToPeriodSummaryDto(
                currentData, currentStart, currentEnd
        );
        
        SalesStatisticsDto.PeriodSummary previousSummary = convertToPeriodSummaryDto(
                previousData, previousStart, previousEnd
        );

        BigDecimal growthRate = calculateGrowthRate(
                previousSummary.getTotalAmount(), 
                currentSummary.getTotalAmount()
        );
        
        String growthType = determineGrowthType(growthRate);

        return SalesStatisticsDto.ComparisonSales.builder()
                .currentPeriod(currentSummary)
                .previousPeriod(previousSummary)
                .growthRate(growthRate)
                .growthType(growthType)
                .build();
    }

    private SalesStatisticsDto.DailySales convertToDailySalesDto(DailyStoreSales dailySales) {
        BigDecimal avgOrderAmount = BigDecimal.ZERO;
        if (dailySales.getOrderCount() > 0) {
            avgOrderAmount = dailySales.getTotalAmount()
                    .divide(BigDecimal.valueOf(dailySales.getOrderCount()), 2, RoundingMode.HALF_UP);
        }

        return SalesStatisticsDto.DailySales.builder()
                .date(dailySales.getSaleDate())
                .orderCount(dailySales.getOrderCount())
                .totalAmount(dailySales.getTotalAmount())
                .avgOrderAmount(avgOrderAmount)
                .build();
    }

    private SalesStatisticsDto.PeriodSummary convertToPeriodSummaryDto(
            Object[] summaryData, LocalDate startDate, LocalDate endDate) {
        
        if (summaryData == null || summaryData[0] == null) {
            return SalesStatisticsDto.PeriodSummary.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalOrderCount(0)
                    .totalAmount(BigDecimal.ZERO)
                    .avgDailyAmount(BigDecimal.ZERO)
                    .avgOrderAmount(BigDecimal.ZERO)
                    .maxDailyAmount(BigDecimal.ZERO)
                    .minDailyAmount(BigDecimal.ZERO)
                    .build();
        }

        Long dayCount = (Long) summaryData[0];
        Long totalOrderCount = (Long) summaryData[1];
        BigDecimal totalAmount = (BigDecimal) summaryData[2];
        BigDecimal avgDailyAmount = (BigDecimal) summaryData[3];
        BigDecimal maxDailyAmount = (BigDecimal) summaryData[4];
        BigDecimal minDailyAmount = (BigDecimal) summaryData[5];

        BigDecimal avgOrderAmount = BigDecimal.ZERO;
        if (totalOrderCount != null && totalOrderCount > 0) {
            avgOrderAmount = totalAmount.divide(
                    BigDecimal.valueOf(totalOrderCount), 2, RoundingMode.HALF_UP
            );
        }

        return SalesStatisticsDto.PeriodSummary.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalOrderCount(totalOrderCount != null ? totalOrderCount.intValue() : 0)
                .totalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO)
                .avgDailyAmount(avgDailyAmount != null ? avgDailyAmount : BigDecimal.ZERO)
                .avgOrderAmount(avgOrderAmount)
                .maxDailyAmount(maxDailyAmount != null ? maxDailyAmount : BigDecimal.ZERO)
                .minDailyAmount(minDailyAmount != null ? minDailyAmount : BigDecimal.ZERO)
                .build();
    }

    private SalesStatisticsDto.TopMenuSales convertToTopMenuSalesDto(Object[] menuData) {
        String menuName = (String) menuData[0];
        Long totalQuantity = (Long) menuData[1];
        BigDecimal totalAmount = (BigDecimal) menuData[2];
        BigDecimal avgPrice = (BigDecimal) menuData[3];

        return SalesStatisticsDto.TopMenuSales.builder()
                .menuName(menuName)
                .totalQuantity(totalQuantity.intValue())
                .totalAmount(totalAmount)
                .avgPrice(avgPrice)
                .build();
    }

    private BigDecimal calculateGrowthRate(BigDecimal previous, BigDecimal current) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private String determineGrowthType(BigDecimal growthRate) {
        if (growthRate.compareTo(BigDecimal.ZERO) > 0) {
            return "증가";
        } else if (growthRate.compareTo(BigDecimal.ZERO) < 0) {
            return "감소";
        } else {
            return "동일";
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new SalesStatisticsException.InvalidDateRangeException("날짜는 필수 입력값입니다.");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new SalesStatisticsException.InvalidDateRangeException("시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }
        
        if (startDate.plusYears(1).isBefore(endDate)) {
            throw new SalesStatisticsException.InvalidDateRangeException("조회 가능한 최대 기간은 1년입니다.");
        }
    }
    
    private void validateStoreId(UUID storeId) {
        if (storeId == null) {
            throw new SalesStatisticsException.StoreNotFoundException("매장 ID는 필수 입력값입니다.");
        }
    }
}
