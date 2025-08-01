package profect.eatcloud.Domain.Customer.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import profect.eatcloud.Domain.Customer.Dto.CartItem;
import profect.eatcloud.Domain.Customer.Dto.request.AddCartItemRequest;
import profect.eatcloud.Domain.Customer.Entity.Cart;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CartRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CartService cartService;

    private final UUID customerId = UUID.randomUUID();

    @Test
    void addItemToEmptyCart_ShouldSucceed() {
        when(cartRepository.findByCustomerId(customerId))
                .thenReturn(Optional.empty());

        AddCartItemRequest request = new AddCartItemRequest(
                UUID.randomUUID(), "Menu1", 2, 10000, UUID.randomUUID()
        );

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(mockCustomer));

        // 새 Cart 객체 생성 및 save 시 반환되도록 설정
        Cart mockCart = new Cart();
        mockCart.setCustomer(mockCustomer);
        mockCart.setCartItems(new ArrayList<>());
        when(cartRepository.save(any(Cart.class))).thenReturn(mockCart);

        assertDoesNotThrow(() -> cartService.addItem(customerId, request));
    }

    @Test
    void addItemFromSameStore_ShouldSucceed() {
        UUID storeId = UUID.randomUUID();
        CartItem existingItem = CartItem.builder()
                .menuId(UUID.randomUUID())
                .menuName("기존메뉴")
                .quantity(1)
                .price(1000)
                .storeId(storeId)
                .build();

        Cart cart = Cart.builder()
                .cartItems(new ArrayList<>(List.of(existingItem)))
                .build();

        when(cartRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(cart));

        AddCartItemRequest request = new AddCartItemRequest(
                UUID.randomUUID(), "다른메뉴", 1, 2000, storeId
        );

        assertDoesNotThrow(() -> cartService.addItem(customerId, request));
    }

    @Test
    void addItemFromDifferentStore_ShouldThrowException() {
        UUID storeA = UUID.randomUUID();
        UUID storeB = UUID.randomUUID();

        CartItem existingItem = CartItem.builder()
                .menuId(UUID.randomUUID())
                .menuName("기존메뉴")
                .quantity(1)
                .price(1000)
                .storeId(storeA)
                .build();

        Cart cart = Cart.builder()
                .cartItems(new ArrayList<>(List.of(existingItem)))
                .build();

        when(cartRepository.findByCustomerId(customerId))
                .thenReturn(Optional.of(cart));

        AddCartItemRequest request = new AddCartItemRequest(
                UUID.randomUUID(), "다른가게메뉴", 1, 2000, storeB
        );

        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(customerId, request));
    }
}