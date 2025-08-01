package profect.eatcloud.Domain.Payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;
import profect.eatcloud.Domain.Payment.Service.PointService;
import profect.eatcloud.Domain.Payment.Service.PointService.PointResult;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PointService pointService;

    @DisplayName("포인트 충분 시 사용 성공")
    @Test
    void givenSufficientPoints_whenUsePoints_thenReturnSuccess() {
        // given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .points(5000)
                .build();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(customer));
        given(customerRepository.save(any(Customer.class)))
                .willReturn(customer);

        // when
        PointResult result = pointService.usePoints(customerId, 2000);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUsedPoints()).isEqualTo(2000);
        assertThat(result.getRemainingPoints()).isEqualTo(3000);
        assertThat(customer.getPoints()).isEqualTo(3000);
        then(customerRepository).should().save(customer);
    }

    @DisplayName("포인트 부족 시 사용 실패")
    @Test
    void givenInsufficientPoints_whenUsePoints_thenReturnFailure() {
        // given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .points(1000)
                .build();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(customer));

        // when
        PointResult result = pointService.usePoints(customerId, 2000);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("포인트가 부족합니다");
        assertThat(customer.getPoints()).isEqualTo(1000); // 원래 포인트 유지
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @DisplayName("존재하지 않는 고객 포인트 사용 실패")
    @Test
    void givenNonExistentCustomer_whenUsePoints_thenReturnFailure() {
        // given
        UUID nonExistentCustomerId = UUID.randomUUID();

        given(customerRepository.findById(nonExistentCustomerId))
                .willReturn(Optional.empty());

        // when
        PointResult result = pointService.usePoints(nonExistentCustomerId, 1000);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("고객을 찾을 수 없습니다");
    }

    @DisplayName("0 포인트 사용 시 고객이 없어서 실패")
    @Test
    void givenZeroPoints_whenUsePoints_thenReturnFailure() {
        // given
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.empty());

        // when
        PointResult result = pointService.usePoints(customerId, 0);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("고객을 찾을 수 없습니다");
    }

    @DisplayName("음수 포인트 사용 시 고객이 없어서 실패")
    @Test
    void givenNegativePoints_whenUsePoints_thenReturnFailure() {
        // given
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.empty());

        // when
        PointResult result = pointService.usePoints(customerId, -1000);

        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("고객을 찾을 수 없습니다");
    }

    @DisplayName("포인트 적립 성공")
    @Test
    void givenValidCustomer_whenRefundPoints_thenReturnSuccess() {
        // given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .points(1000)
                .build();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(customer));
        given(customerRepository.save(any(Customer.class)))
                .willReturn(customer);

        // when
        PointResult result = pointService.refundPoints(customerId, 500);

        // then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUsedPoints()).isEqualTo(500);
        assertThat(result.getRemainingPoints()).isEqualTo(1500);
        assertThat(customer.getPoints()).isEqualTo(1500);
        then(customerRepository).should().save(customer);
    }

    @DisplayName("포인트 사용 가능 여부 확인 - 충분한 포인트")
    @Test
    void givenSufficientPoints_whenCanUsePoints_thenReturnTrue() {
        // given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .points(3000)
                .build();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(customer));

        // when
        boolean canUse = pointService.canUsePoints(customerId, 2000);

        // then
        assertThat(canUse).isTrue();
    }

    @DisplayName("포인트 사용 가능 여부 확인 - 부족한 포인트")
    @Test
    void givenInsufficientPoints_whenCanUsePoints_thenReturnFalse() {
        // given
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(customerId)
                .points(1000)
                .build();

        given(customerRepository.findById(customerId))
                .willReturn(Optional.of(customer));

        // when
        boolean canUse = pointService.canUsePoints(customerId, 2000);

        // then
        assertThat(canUse).isFalse();
    }
}