package profect.eatcloud.Domain.Customer.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Domain.Customer.Dto.CartItem;
import profect.eatcloud.Domain.Customer.Dto.request.AddCartItemRequest;
import profect.eatcloud.Domain.Customer.Dto.request.UpdateCartItemRequest;
import profect.eatcloud.Domain.Customer.Service.CartService;
import profect.eatcloud.Security.SecurityUtil;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers/cart")
@Tag(name = "3. CartController", description = "장바구니 관리 API")
@Slf4j
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 추가", description = "장바구니에 메뉴를 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<Void> addItem(@RequestBody AddCartItemRequest request) {
        try {
            String customerIdStr = SecurityUtil.getCurrentUsername();
            UUID customerId = UUID.fromString(customerIdStr);
            cartService.addItem(customerId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to add item to cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "장바구니 조회", description = "장바구니를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        try {
            String customerIdStr = SecurityUtil.getCurrentUsername();
            UUID customerId = UUID.fromString(customerIdStr);
            List<CartItem> cartItems = cartService.getCart(customerId);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            log.error("Failed to get cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "장바구니 메뉴 수량 변경", description = "장바구니에 담긴 메뉴의 수량을 변경합니다.")
    @PatchMapping("/update")
    public ResponseEntity<Void> updateQuantity(@RequestBody UpdateCartItemRequest request) {
        try {
            String customerIdStr = SecurityUtil.getCurrentUsername();
            UUID customerId = UUID.fromString(customerIdStr);
            cartService.updateItemQuantity(customerId, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update cart item quantity", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "장바구니 메뉴 개별 삭제", description = "장바구니에 담긴 메뉴를 삭제합니다.")
    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID menuId) {
        try {
            String customerIdStr = SecurityUtil.getCurrentUsername();
            UUID customerId = UUID.fromString(customerIdStr);
            cartService.removeItem(customerId, menuId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to remove item from cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "장바구니 전체 삭제", description = "장바구니에 담긴 모든 메뉴를 삭제합니다.")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        try {
            String customerIdStr = SecurityUtil.getCurrentUsername();
            UUID customerId = UUID.fromString(customerIdStr);
            cartService.clearCart(customerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to clear cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
