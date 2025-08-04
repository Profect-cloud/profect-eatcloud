package profect.eatcloud.Domain.Customer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import profect.eatcloud.Domain.Customer.Dto.CartItem;
import profect.eatcloud.Domain.Customer.Dto.request.AddCartItemRequest;
import profect.eatcloud.Domain.Customer.Dto.request.UpdateCartItemRequest;
import profect.eatcloud.Domain.Customer.Entity.Cart;
import profect.eatcloud.Domain.Customer.Entity.Customer;
import profect.eatcloud.Domain.Customer.repository.CartRepository;
import profect.eatcloud.Domain.Customer.repository.CustomerRepository;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofHours(24);

    public void addItem(UUID customerId, AddCartItemRequest request) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(request, "Add cart item request cannot be null");
        validateAddItemRequest(request);

        try {
            List<CartItem> cartItems = getCart(customerId);

            validateStoreConsistency(cartItems, request.getStoreId());

            Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getMenuId().equals(request.getMenuId()))
                .findFirst();

            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + request.getQuantity());
                log.debug("Updated existing cart item: customerId={}, menuId={}, newQuantity={}",
                    customerId, request.getMenuId(), item.getQuantity());
            } else {
                CartItem newItem = CartItem.builder()
                    .menuId(request.getMenuId())
                    .menuName(request.getMenuName())
                    .quantity(request.getQuantity())
                    .price(request.getPrice())
                    .storeId(request.getStoreId())
                    .build();
                cartItems.add(newItem);
                log.debug("Added new cart item: customerId={}, menuId={}, quantity={}",
                    customerId, request.getMenuId(), request.getQuantity());
            }

            saveCartToRedis(customerId, cartItems);
            syncToDatabaseAsync(customerId, cartItems);

            log.info("Successfully added item to cart: customerId={}, menuId={}",
                customerId, request.getMenuId());

        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for adding item to cart: customerId={}, error={}", customerId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to add item to cart for customer: {}", customerId, e);
            throw new RuntimeException("장바구니에 상품을 추가하는데 실패했습니다.", e);
        }
    }

    public List<CartItem> getCart(UUID customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        try {
            List<CartItem> cartItems = getCartFromRedis(customerId);

            if (!cartItems.isEmpty()) {
                log.debug("Cart found in Redis for customer: {}, itemCount={}",
                    customerId, cartItems.size());
                return cartItems;
            }

            log.debug("Cache miss, retrieving from database for customer: {}", customerId);
            cartItems = getCartFromDatabase(customerId);

            if (!cartItems.isEmpty()) {
                saveCartToRedis(customerId, cartItems);
            }

            return cartItems;

        } catch (Exception e) {
            log.error("Failed to get cart for customer: {}", customerId, e);
            return getCartFromDatabase(customerId);
        }
    }

    public void updateItemQuantity(UUID customerId, UpdateCartItemRequest request) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(request, "Update cart item request cannot be null");
        validateUpdateItemRequest(request);

        try {
            List<CartItem> cartItems = getCart(customerId);

            CartItem targetItem = cartItems.stream()
                .filter(item -> item.getMenuId().equals(request.getMenuId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴가 장바구니에 없습니다."));

            if (request.getQuantity() <= 0) {
                cartItems.remove(targetItem);
                log.debug("Removed item from cart: customerId={}, menuId={}",
                    customerId, request.getMenuId());
            } else {
                targetItem.setQuantity(request.getQuantity());
                log.debug("Updated item quantity: customerId={}, menuId={}, quantity={}",
                    customerId, request.getMenuId(), request.getQuantity());
            }

            saveCartToRedis(customerId, cartItems);
            syncToDatabaseAsync(customerId, cartItems);

            log.info("Successfully updated cart item: customerId={}, menuId={}",
                customerId, request.getMenuId());

        } catch (Exception e) {
            log.error("Failed to update cart item quantity for customer: {}", customerId, e);
            throw new RuntimeException("장바구니 상품 수정에 실패했습니다.", e);
        }
    }

    public void removeItem(UUID customerId, UUID menuId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(menuId, "Menu ID cannot be null");

        try {
            List<CartItem> cartItems = getCart(customerId);

            boolean removed = cartItems.removeIf(item -> item.getMenuId().equals(menuId));

            if (!removed) {
                throw new NoSuchElementException("해당 메뉴가 장바구니에 없습니다.");
            }

            saveCartToRedis(customerId, cartItems);
            syncToDatabaseAsync(customerId, cartItems);

            log.info("Successfully removed item from cart: customerId={}, menuId={}",
                customerId, menuId);

        } catch (Exception e) {
            log.error("Failed to remove item from cart for customer: {}", customerId, e);
            throw new RuntimeException("장바구니에서 상품 제거에 실패했습니다.", e);
        }
    }

    @Transactional
    public void clearCart(UUID customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        try {
            invalidateCartCache(customerId);
            clearCartFromDatabase(customerId);

            log.info("Successfully cleared cart for customer: {}", customerId);

        } catch (Exception e) {
            log.error("Failed to clear cart for customer: {}", customerId, e);
            throw new RuntimeException("장바구니 비우기에 실패했습니다.", e);
        }
    }

    @Transactional
    public void invalidateCartAfterOrder(UUID customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        try {
            invalidateCartCache(customerId);
            clearCartFromDatabase(customerId);
            log.info("Cart invalidated after order completion for customer: {}", customerId);
        } catch (Exception e) {
            log.error("Failed to invalidate cart after order for customer: {}", customerId, e);
        }
    }

    public boolean isRedisHealthy() {
        return isRedisAvailable();
    }

    public CartStats getCartStats(UUID customerId) {
        try {
            List<CartItem> cartItems = getCart(customerId);

            int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            int totalAmount = cartItems.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity()).sum();

            return CartStats.builder()
                .itemCount(totalItems)
                .totalAmount(totalAmount)
                .menuCount(cartItems.size())
                .dataSource(isRedisAvailable() ? "Redis" : "Database")
                .build();

        } catch (Exception e) {
            log.warn("Failed to get cart stats for customer: {}", customerId, e);
            return CartStats.builder()
                .itemCount(0)
                .totalAmount(0)
                .menuCount(0)
                .dataSource("Error")
                .build();
        }
    }

    private String getCartKey(UUID customerId) {
        return CART_KEY_PREFIX + customerId.toString();
    }

    private boolean isRedisAvailable() {
        try {
			assert redisTemplate.getConnectionFactory() != null;
			redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (RedisConnectionFailureException e) {
            log.debug("Redis connection failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("Redis availability check failed: {}", e.getMessage());
            return false;
        }
    }

    private List<CartItem> getCartFromRedis(UUID customerId) {
        try {
            if (!isRedisAvailable()) {
                return new ArrayList<>();
            }

            String cartKey = getCartKey(customerId);
            Object cartData = redisTemplate.opsForValue().get(cartKey);

            if (cartData instanceof List<?> rawList) {
                List<CartItem> cartItems = new ArrayList<>();

                for (Object item : rawList) {
                    if (item instanceof CartItem) {
                        cartItems.add((CartItem) item);
                    } else if (item instanceof Map) {
                        cartItems.add(convertMapToCartItem((Map<?, ?>) item));
                    }
                }

                return cartItems;
            }

            return new ArrayList<>();

        } catch (RedisConnectionFailureException e) {
            log.warn("Redis unavailable, falling back to database for customer: {}", customerId);
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("Failed to get cart from Redis for customer: {}, error: {}",
                customerId, e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveCartToRedis(UUID customerId, List<CartItem> cartItems) {
        try {
            if (!isRedisAvailable()) {
                log.debug("Redis unavailable, skipping cache update for customer: {}", customerId);
                return;
            }

            String cartKey = getCartKey(customerId);

            if (cartItems.isEmpty()) {
                redisTemplate.delete(cartKey);
                log.debug("Deleted empty cart from cache for customer: {}", customerId);
            } else {
                redisTemplate.opsForValue().set(cartKey, cartItems, CART_TTL);
                log.debug("Saved cart to cache for customer: {}, itemCount={}",
                    customerId, cartItems.size());
            }

        } catch (RedisConnectionFailureException e) {
            log.warn("Redis unavailable during cart save for customer: {}", customerId);
        } catch (Exception e) {
            log.error("Failed to save cart to Redis for customer: {}", customerId, e);
        }
    }

    private void invalidateCartCache(UUID customerId) {
        try {
            if (isRedisAvailable()) {
                String cartKey = getCartKey(customerId);
                redisTemplate.delete(cartKey);
                log.debug("Invalidated cart cache for customer: {}", customerId);
            }
        } catch (Exception e) {
            log.warn("Failed to invalidate cart cache for customer: {}", customerId, e);
        }
    }

    private List<CartItem> getCartFromDatabase(UUID customerId) {
        try {
            Optional<Cart> cartOptional = cartRepository.findByCustomerId(customerId);

            if (cartOptional.isPresent()) {
                Cart cart = cartOptional.get();
                List<CartItem> items = convertCartEntityToItems(cart);
                log.debug("Retrieved cart from database for customer: {}, itemCount={}",
                    customerId, items.size());
                return items;
            }

            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Failed to get cart from database for customer: {}", customerId, e);
            return new ArrayList<>();
        }
    }

    private void syncToDatabaseAsync(UUID customerId, List<CartItem> cartItems) {
        try {
            CompletableFuture.runAsync(() -> syncToDatabase(customerId, cartItems))
                .exceptionally(throwable -> {
                    log.error("Async database sync failed for customer: {}", customerId, throwable);
                    return null;
                });
        } catch (Exception e) {
            log.warn("Failed to start async DB sync for customer: {}, falling back to sync",
                customerId, e);
            syncToDatabase(customerId, cartItems);
        }
    }

    @Transactional
    protected void syncToDatabase(UUID customerId, List<CartItem> cartItems) {
        try {
            if (cartItems.isEmpty()) {
                cartRepository.deleteByCustomerId(customerId);
                log.debug("Deleted empty cart from database for customer: {}", customerId);
            } else {
                Cart cart = convertCartItemsToEntity(customerId, cartItems);
                cartRepository.save(cart);
                log.debug("Synced cart to database for customer: {}, itemCount={}",
                    customerId, cartItems.size());
            }

        } catch (Exception e) {
            log.error("Failed to sync cart to database for customer: {}", customerId, e);
            throw new RuntimeException("장바구니 데이터베이스 동기화에 실패했습니다.", e);
        }
    }

    private void clearCartFromDatabase(UUID customerId) {
        try {
            cartRepository.deleteByCustomerId(customerId);
            log.debug("Cleared cart from database for customer: {}", customerId);
        } catch (Exception e) {
            log.error("Failed to clear cart from database for customer: {}", customerId, e);
            throw new RuntimeException("데이터베이스 장바구니 삭제에 실패했습니다.", e);
        }
    }

    private CartItem convertMapToCartItem(Map<?, ?> map) {
        try {
            return CartItem.builder()
                .menuId(UUID.fromString(map.get("menuId").toString()))
                .menuName(map.get("menuName").toString())
                .quantity(Integer.valueOf(map.get("quantity").toString()))
                .price(Integer.valueOf(map.get("price").toString()))
                .storeId(UUID.fromString(map.get("storeId").toString()))
                .build();
        } catch (Exception e) {
            log.error("Failed to convert map to CartItem: {}", map, e);
            throw new RuntimeException("장바구니 데이터 변환에 실패했습니다.", e);
        }
    }

    private List<CartItem> convertCartEntityToItems(Cart cart) {
        if (cart == null || cart.getCartItems() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(cart.getCartItems());
    }

    private Cart convertCartItemsToEntity(UUID customerId, List<CartItem> cartItems) {
        Optional<Cart> existingCart = cartRepository.findByCustomerId(customerId);
        Cart cart;

        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setCartItems(new ArrayList<>(cartItems));
        } else {
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer not found with ID: " + customerId));

            cart = Cart.builder()
                .cartItems(new ArrayList<>(cartItems))
                .customer(customer)
                .build();
        }

        return cart;
    }

    private void validateAddItemRequest(AddCartItemRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        if (request.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }
        if (request.getMenuName() == null || request.getMenuName().trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴명이 필요합니다.");
        }
    }

    private void validateUpdateItemRequest(UpdateCartItemRequest request) {
        if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }
    }

    private void validateStoreConsistency(List<CartItem> cartItems, UUID newStoreId) {
        if (!cartItems.isEmpty()) {
            UUID existingStoreId = cartItems.getFirst().getStoreId();
            if (!existingStoreId.equals(newStoreId)) {
                throw new IllegalArgumentException("다른 가게의 메뉴는 장바구니에 추가할 수 없습니다. 기존 장바구니를 비운 후 다시 시도해주세요.");
            }
        }
    }

    @lombok.Builder
    @lombok.Getter
    public static class CartStats {
        private final int itemCount;
        private final int totalAmount;
        private final int menuCount;
        private final String dataSource;

        @Override
        public String toString() {
            return String.format("CartStats{items=%d, menus=%d, total=%d원, source=%s}",
                itemCount, menuCount, totalAmount, dataSource);
        }
    }
}