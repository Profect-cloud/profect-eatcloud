package profect.eatcloud.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.domain.order.dto.AdminOrderConfirmRequestDto;
import profect.eatcloud.domain.order.dto.AdminOrderCompleteRequestDto;
import profect.eatcloud.domain.order.dto.AdminOrderResponseDto;
import profect.eatcloud.domain.order.service.AdminOrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/orders")
@Slf4j
@Tag(name = "Manager - Order", description = "사장님용 주문 관리 API")
public class ManagerOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @PostMapping("/confirm")
    @Operation(summary = "주문 수락", description = "결제 완료된 주문을 수락합니다. (PAID → CONFIRMED)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AdminOrderResponseDto> confirmOrder(
            @RequestBody AdminOrderConfirmRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("주문 수락 요청: orderId={}, 요청자={}", request.getOrderId(), userDetails.getUsername());
        
        try {
            AdminOrderResponseDto response = adminOrderService.confirmOrder(request.getOrderId());
            log.info("주문 수락 완료: orderId={}, orderNumber={}, 처리자={}", 
                    response.getOrderId(), response.getOrderNumber(), userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("주문 수락 실패: orderId={}, 요청자={}, error={}", 
                     request.getOrderId(), userDetails.getUsername(), e.getMessage());
            
            AdminOrderResponseDto errorResponse = AdminOrderResponseDto.builder()
                    .orderId(request.getOrderId())
                    .orderNumber(null)
                    .orderStatus("ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/complete")
    @Operation(summary = "주문 완료", description = "수락된 주문을 완료 처리합니다. (CONFIRMED → COMPLETED)")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AdminOrderResponseDto> completeOrder(
            @RequestBody AdminOrderCompleteRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("주문 완료 요청: orderId={}, 요청자={}", request.getOrderId(), userDetails.getUsername());
        
        try {
            AdminOrderResponseDto response = adminOrderService.completeOrder(request.getOrderId());
            log.info("주문 완료 처리 완료: orderId={}, orderNumber={}, 처리자={}", 
                    response.getOrderId(), response.getOrderNumber(), userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("주문 완료 실패: orderId={}, 요청자={}, error={}", 
                     request.getOrderId(), userDetails.getUsername(), e.getMessage());
            
            AdminOrderResponseDto errorResponse = AdminOrderResponseDto.builder()
                    .orderId(request.getOrderId())
                    .orderNumber(null)
                    .orderStatus("ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 조회", description = "주문 ID로 현재 주문 상태를 조회합니다.")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AdminOrderResponseDto> getOrderStatus(
            @Parameter(description = "주문 ID") @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("주문 상태 조회 요청: orderId={}, 요청자={}", orderId, userDetails.getUsername());
        
        try {
            AdminOrderResponseDto response = adminOrderService.getOrderStatus(orderId);
            log.info("주문 상태 조회 완료: orderId={}, status={}, 요청자={}", 
                    orderId, response.getOrderStatus(), userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("주문 상태 조회 실패: orderId={}, 요청자={}, error={}", 
                     orderId, userDetails.getUsername(), e.getMessage());
            
            AdminOrderResponseDto errorResponse = AdminOrderResponseDto.builder()
                    .orderId(orderId)
                    .orderNumber(null)
                    .orderStatus("ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
