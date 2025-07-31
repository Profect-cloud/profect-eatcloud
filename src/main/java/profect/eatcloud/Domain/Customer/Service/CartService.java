package profect.eatcloud.Domain.Customer.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Customer.Dto.CartItem;
import profect.eatcloud.Domain.Customer.Dto.request.AddCartItemRequest;
import profect.eatcloud.Domain.Customer.Dto.request.UpdateCartItemRequest;
import profect.eatcloud.Domain.Customer.Entity.Cart;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CartRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

    private Cart createNewCart(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart newCart = Cart.builder()
                .cartItems(new ArrayList<>())
                .customer(customer)
                .build();

        return cartRepository.save(newCart);
    }

    public void addItem(UUID customerId, AddCartItemRequest request) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> createNewCart(customerId));

        List<CartItem> cartItems = cart.getCartItems();

        // 장바구니에 이미 아이템이 있다면 가게 ID가 일치하는지 확인
        if (!cartItems.isEmpty()) {
            UUID existingStoreId = cartItems.get(0).getStoreId(); // 기존 아이템의 storeId
            if (!existingStoreId.equals(request.getStoreId())) {
                throw new IllegalArgumentException("장바구니에는 한 가게의 메뉴만 담을 수 있습니다.");
            }
        }

        // 같은 메뉴가 있는 경우 수량 증가
        Optional<CartItem> existing = cartItems.stream()
                .filter(item -> item.getMenuId().equals(request.getMenuId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.getQuantity());
        } else {
            cartItems.add(CartItem.builder()
                    .menuId(request.getMenuId())
                    .menuName(request.getMenuName())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .storeId(request.getStoreId()) // 필수!
                    .build());
        }

        cartRepository.save(cart);
    }

    public List<CartItem> getCart(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
                .map(Cart::getCartItems)
                .orElse(Collections.emptyList());
    }

    public void updateItemQuantity(UUID customerId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getCartItems().forEach(item -> {
            if (item.getMenuId().equals(request.getMenuId())) {
                item.setQuantity(request.getQuantity());
            }
        });

        cartRepository.save(cart);
    }

    public void removeItem(UUID customerId, UUID menuId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setCartItems(
                cart.getCartItems().stream()
                        .filter(item -> !item.getMenuId().equals(menuId))
                        .collect(Collectors.toList())
        );

        cartRepository.save(cart);
    }

    public void clearCart(UUID customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.setCartItems(new ArrayList<>());
        cartRepository.save(cart);
    }
}
