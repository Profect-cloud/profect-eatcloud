package profect.eatcloud.Domain.User.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart/items")
public class CartController {
    @PostMapping
    public void addCartItem(org.springframework.security.core.Authentication authentication) {}

    @PatchMapping("/{quantity}")
    public void updateCartItemQuantity(org.springframework.security.core.Authentication authentication, @PathVariable int quantity) {}

    @DeleteMapping("/all")
    public void deleteAllCartItems(org.springframework.security.core.Authentication authentication) {}

    @DeleteMapping("/{storeId}/{menuNum}")
    public void deleteCartItem(org.springframework.security.core.Authentication authentication, @PathVariable String storeId, @PathVariable int menuNum) {}

    @GetMapping
    public void getCartItems(org.springframework.security.core.Authentication authentication) {}
} 