package profect.eatcloud.Domain.Store.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Store.Dto.SalesStatisticsDto;
import profect.eatcloud.Domain.Store.Service.SalesStatisticsService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/sales")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Store Sales Statistics", description = "매장 매출 통계 API")
public class SalesStatisticsController {

    private final SalesStatisticsService salesStatisticsService;

    @GetMapping("/statistics")
    @Operation(summary = "기간별 매출 통계 조회", description = "특정 기간의 매장 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.SalesResponse> getSalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId,
            
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("매출 통계 조회 요청 - 매장ID: {}, 기간: {} ~ {}", storeId, startDate, endDate);
        
        SalesStatisticsDto.SalesResponse response = salesStatisticsService
                .getSalesStatistics(storeId, startDate, endDate);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/recent")
    @Operation(summary = "최근 N일간 매출 통계 조회", description = "최근 N일간의 매장 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.SalesResponse> getRecentSalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId,
            
            @Parameter(description = "최근 일수 (기본값: 7일)")
            @RequestParam(defaultValue = "7") int days) {

        log.info("최근 매출 통계 조회 요청 - 매장ID: {}, 기간: {}일", storeId, days);
        
        SalesStatisticsDto.SalesResponse response = salesStatisticsService
                .getRecentSalesStatistics(storeId, days);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/menu-statistics")
    @Operation(summary = "메뉴별 매출 통계 조회", description = "특정 기간의 메뉴별 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.MenuSalesResponse> getMenuSalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId,
            
            @Parameter(description = "시작 날짜 (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "종료 날짜 (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "상위 메뉴 개수 제한 (기본값: 10개)")
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("메뉴별 매출 통계 조회 요청 - 매장ID: {}, 기간: {} ~ {}, 제한: {}개", 
                storeId, startDate, endDate, limit);
        
        SalesStatisticsDto.MenuSalesResponse response = salesStatisticsService
                .getMenuSalesStatistics(storeId, startDate, endDate, limit);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comparison")
    @Operation(summary = "기간별 매출 비교 통계", description = "현재 기간과 이전 기간의 매출을 비교합니다.")
    public ResponseEntity<SalesStatisticsDto.ComparisonSales> getComparisonSalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId,
            
            @Parameter(description = "비교 기간 시작 날짜 (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "비교 기간 종료 날짜 (YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("매출 비교 통계 조회 요청 - 매장ID: {}, 기간: {} ~ {}", storeId, startDate, endDate);
        
        SalesStatisticsDto.ComparisonSales response = salesStatisticsService
                .getComparisonSalesStatistics(storeId, startDate, endDate);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/weekly")
    @Operation(summary = "주간 매출 통계", description = "최근 4주간의 주별 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.SalesResponse> getWeeklySalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId) {

        log.info("주간 매출 통계 조회 요청 - 매장ID: {}", storeId);
        
        SalesStatisticsDto.SalesResponse response = salesStatisticsService
                .getRecentSalesStatistics(storeId, 28);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/monthly")
    @Operation(summary = "월간 매출 통계", description = "최근 3개월간의 월별 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.SalesResponse> getMonthlySalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId) {

        log.info("월간 매출 통계 조회 요청 - 매장ID: {}", storeId);
        
        SalesStatisticsDto.SalesResponse response = salesStatisticsService
                .getRecentSalesStatistics(storeId, 90);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/today")
    @Operation(summary = "오늘 매출 통계", description = "오늘의 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.SalesResponse> getTodaySalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId) {

        log.info("오늘 매출 통계 조회 요청 - 매장ID: {}", storeId);
        
        LocalDate today = LocalDate.now();
        SalesStatisticsDto.SalesResponse response = salesStatisticsService
                .getSalesStatistics(storeId, today, today);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/yesterday")
    @Operation(summary = "어제 매출 통계", description = "어제의 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatisticsDto.SalesResponse> getYesterdaySalesStatistics(
            @Parameter(description = "매장 ID") 
            @PathVariable UUID storeId) {

        log.info("어제 매출 통계 조회 요청 - 매장ID: {}", storeId);
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        SalesStatisticsDto.SalesResponse response = salesStatisticsService
                .getSalesStatistics(storeId, yesterday, yesterday);
        
        return ResponseEntity.ok(response);
    }
}
