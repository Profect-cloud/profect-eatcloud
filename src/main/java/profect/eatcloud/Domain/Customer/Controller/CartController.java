package profect.eatcloud.Domain.Customer.Controller;

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
@RequestMapping("api/v1/customers/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Void> addItem(@RequestBody AddCartItemRequest request) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.addItem(customerId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateQuantity(@RequestBody UpdateCartItemRequest request) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.updateItemQuantity(customerId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<Void> removeItem(@PathVariable UUID menuId) {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.removeItem(customerId, menuId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        String customerIdStr = SecurityUtil.getCurrentUsername();
        UUID customerId = UUID.fromString(customerIdStr);
        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}
