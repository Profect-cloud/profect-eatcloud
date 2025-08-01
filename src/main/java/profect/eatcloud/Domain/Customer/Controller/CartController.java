package profect.eatcloud.Domain.Customer.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 추가", description = "장바구니에 메뉴를 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<Void> addItem(@RequestBody AddCartItemRequest request) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.addItem(customerId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장바구니 조회", description = "장바구니를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @Operation(summary = "장바구니 메뉴 수량 변경", description = "장바구니에 담긴 메뉴의 수량을 변경합니다.")
    @PatchMapping("/update")
    public ResponseEntity<Void> updateQuantity(@RequestBody UpdateCartItemRequest request) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.updateItemQuantity(customerId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장바구니 메뉴 개별 삭제", description = "장바구니에 담긴 메뉴를 삭제합니다.")
    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID menuId) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.removeItem(customerId, menuId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "장바구니 전체 삭제", description = "장바구니에 담긴 모든 메뉴를 삭제합니다.")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}
