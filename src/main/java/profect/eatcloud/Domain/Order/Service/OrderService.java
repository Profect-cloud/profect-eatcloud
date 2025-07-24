package profect.eatcloud.Domain.Order.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import profect.eatcloud.Domain.Order.Dto.OrderRequestDto;
import profect.eatcloud.Domain.Order.Dto.OrderResponseDto;
import profect.eatcloud.Domain.Order.Entity.Order;
import profect.eatcloud.Domain.Order.Repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
} 