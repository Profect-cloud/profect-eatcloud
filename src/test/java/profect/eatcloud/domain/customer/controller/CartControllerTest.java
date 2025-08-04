package profect.eatcloud.domain.customer.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import profect.eatcloud.domain.customer.dto.CartItem;
import profect.eatcloud.domain.customer.dto.request.AddCartItemRequest;
import profect.eatcloud.domain.customer.dto.request.UpdateCartItemRequest;
import profect.eatcloud.domain.customer.service.CartService;
import profect.eatcloud.security.SecurityUtil;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private UUID customerId;

    private MockedStatic<SecurityUtil> securityUtilMock;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        securityUtilMock = Mockito.mockStatic(SecurityUtil.class);
        securityUtilMock.when(SecurityUtil::getCurrentUsername).thenReturn(customerId.toString());
    }

    @AfterEach
    void tearDown() {
        securityUtilMock.close();
    }

    @Test
    @DisplayName("장바구니 항목 추가 성공")
    void addItem_success() {
        AddCartItemRequest request = new AddCartItemRequest(
                UUID.randomUUID(),
                "테스트메뉴",
                3,
                10000,
                UUID.randomUUID()
        );

        ResponseEntity<Void> response = cartController.addItem(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(cartService, times(1)).addItem(customerId, request);
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCart_success() {
        List<CartItem> mockCartItems = List.of(
                new CartItem(UUID.randomUUID(), "메뉴1", 2, 15000, UUID.randomUUID())
        );

        when(cartService.getCart(customerId)).thenReturn(mockCartItems);

        ResponseEntity<List<CartItem>> response = cartController.getCart();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(mockCartItems);
        verify(cartService, times(1)).getCart(customerId);
    }

    @Test
    @DisplayName("장바구니 메뉴 수량 변경 성공")
    void updateQuantity_success() {
        UpdateCartItemRequest request = new UpdateCartItemRequest(
                UUID.randomUUID(),
                5
        );

        ResponseEntity<Void> response = cartController.updateQuantity(request);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(cartService, times(1)).updateItemQuantity(customerId, request);
    }

    @Test
    @DisplayName("장바구니 개별 메뉴 삭제 성공")
    void removeItem_success() {
        UUID menuId = UUID.randomUUID();

        ResponseEntity<Void> response = cartController.removeItem(menuId);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(cartService, times(1)).removeItem(customerId, menuId);
    }

    @Test
    @DisplayName("장바구니 전체 삭제 성공")
    void clearCart_success() {
        ResponseEntity<Void> response = cartController.clearCart();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(cartService, times(1)).clearCart(customerId);
    }
}