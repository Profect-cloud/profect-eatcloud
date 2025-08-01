package profect.eatcloud.Domain.Customer.Service;

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
import profect.eatcloud.Domain.Customer.Repository.CartRepository;
import profect.eatcloud.Domain.Customer.Repository.CustomerRepository;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;

    private static final String CART_KEY_PREFIX = "cart:";
    private static final String CART_LOCK_PREFIX = "cart_lock:";
    private static final Duration CART_TTL = Duration.ofHours(24);
    private static final Duration LOCK_TTL = Duration.ofSeconds(10);


    public void addItem(UUID customerId, AddCartItemRequest request) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(request, "Add cart item request cannot be null");

        String lockKey = getLockKey(customerId);

        try {
            if (!acquireLock(lockKey)) {
                throw new RuntimeException("다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }

            List<CartItem> cartItems = getCartFromRedis(customerId);

            if (cartItems.isEmpty()) {
                cartItems = getCartFromDatabase(customerId);
            }

            Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getMenuId().equals(request.getMenuId()))
                .findFirst();

            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(item.getQuantity() + request.getQuantity());
            } else {
                CartItem newItem = CartItem.builder()
                    .menuId(request.getMenuId())
                    .menuName(request.getMenuName())
                    .quantity(request.getQuantity())
                    .price(request.getPrice())
                    .storeId(request.getStoreId())
                    .build();
                cartItems.add(newItem);
            }

            saveCartToRedis(customerId, cartItems);

            syncToDatabaseWithTransaction(customerId, cartItems);

        } catch (Exception e) {
            log.error("Failed to add item to cart for customer: {}", customerId, e);
            throw new RuntimeException("카트에 아이템을 추가하는데 실패했습니다.", e);
        } finally {
            releaseLock(lockKey);
        }
    }


    public List<CartItem> getCart(UUID customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        try {
            List<CartItem> cartItems = getCartFromRedis(customerId);

            if (!cartItems.isEmpty()) {
                log.debug("Cart found in Redis for customer: {}", customerId);
                return cartItems;
            }

            log.debug("Cart not found in Redis, checking database for customer: {}", customerId);
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

        String lockKey = getLockKey(customerId);

        try {
            if (!acquireLock(lockKey)) {
                throw new RuntimeException("다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }

            List<CartItem> cartItems = getCartFromRedis(customerId);

            if (cartItems.isEmpty()) {
                cartItems = getCartFromDatabase(customerId);
            }

            CartItem targetItem = cartItems.stream()
                .filter(item -> item.getMenuId().equals(request.getMenuId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 메뉴가 카트에 없습니다."));

            if (request.getQuantity() <= 0) {
                cartItems.remove(targetItem);
            } else {
                targetItem.setQuantity(request.getQuantity());
            }

            saveCartToRedis(customerId, cartItems);
            syncToDatabaseWithTransaction(customerId, cartItems);

        } catch (Exception e) {
            log.error("Failed to update cart item quantity for customer: {}", customerId, e);
            throw new RuntimeException("카트 아이템 수량 업데이트에 실패했습니다.", e);
        } finally {
            releaseLock(lockKey);
        }
    }

    public void removeItem(UUID customerId, UUID menuId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");
        Objects.requireNonNull(menuId, "Menu ID cannot be null");

        String lockKey = getLockKey(customerId);

        try {
            if (!acquireLock(lockKey)) {
                throw new RuntimeException("다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }

            List<CartItem> cartItems = getCartFromRedis(customerId);

            if (cartItems.isEmpty()) {
                cartItems = getCartFromDatabase(customerId);
            }

            boolean removed = cartItems.removeIf(item -> item.getMenuId().equals(menuId));

            if (!removed) {
                throw new NoSuchElementException("해당 메뉴가 카트에 없습니다.");
            }

            saveCartToRedis(customerId, cartItems);
            syncToDatabaseWithTransaction(customerId, cartItems);

        } catch (Exception e) {
            log.error("Failed to remove item from cart for customer: {}", customerId, e);
            throw new RuntimeException("카트에서 아이템 제거에 실패했습니다.", e);
        } finally {
            releaseLock(lockKey);
        }
    }


    @Transactional
    public void clearCart(UUID customerId) {
        Objects.requireNonNull(customerId, "Customer ID cannot be null");

        String lockKey = getLockKey(customerId);

        try {
            if (!acquireLock(lockKey)) {
                throw new RuntimeException("다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }

            invalidateCartCache(customerId);
            clearCartFromDatabase(customerId);

            log.info("Cart cleared for customer: {}", customerId);

        } catch (Exception e) {
            log.error("Failed to clear cart for customer: {}", customerId, e);
            throw new RuntimeException("카트 비우기에 실패했습니다.", e);
        } finally {
            releaseLock(lockKey);
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

    public String getCartStats(UUID customerId) {
        try {
            List<CartItem> cartItems = getCart(customerId);
            int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            int totalAmount = cartItems.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();

            return String.format("Items: %d, Total: %d원, Source: %s",
                totalItems, totalAmount, isRedisAvailable() ? "Redis" : "Database");
        } catch (Exception e) {
            return "Error retrieving cart stats: " + e.getMessage();
        }
    }


    private String getCartKey(UUID customerId) {
        return CART_KEY_PREFIX + customerId.toString();
    }

    private String getLockKey(UUID customerId) {
        return CART_LOCK_PREFIX + customerId.toString();
    }

    private boolean acquireLock(String lockKey) {
        try {
            if (!isRedisAvailable()) {
                return true;
            }

            Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", LOCK_TTL);
            return Boolean.TRUE.equals(lockAcquired);
        } catch (Exception e) {
            log.warn("Failed to acquire lock: {}", lockKey, e);
            return true;
        }
    }

    private void releaseLock(String lockKey) {
        try {
            if (isRedisAvailable()) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            log.warn("Failed to release lock: {}", lockKey, e);
        }
    }

    private boolean isRedisAvailable() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis connection failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.warn("Redis availability check failed: {}", e.getMessage());
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
            log.warn("Redis is unavailable, falling back to database for customer: {}", customerId);
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("Failed to get cart from Redis for customer: {}, error: {}", customerId, e.getMessage());
            return new ArrayList<>();
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
            throw new RuntimeException("카트 데이터 변환에 실패했습니다.", e);
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
            } else {
                redisTemplate.opsForValue().set(cartKey, cartItems, CART_TTL);
            }

            log.debug("Cart saved to Redis for customer: {}", customerId);

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
                log.debug("Cart cache invalidated for customer: {}", customerId);
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
                return convertCartEntityToItems(cart);
            }

            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Failed to get cart from database for customer: {}", customerId, e);
            return new ArrayList<>();
        }
    }

    @Transactional
    protected void syncToDatabaseWithTransaction(UUID customerId, List<CartItem> cartItems) {
        try {
            if (cartItems.isEmpty()) {
                cartRepository.deleteByCustomerId(customerId);
            } else {
                Cart cart = convertCartItemsToEntity(customerId, cartItems);
                cartRepository.save(cart);
            }

            log.debug("Cart synced to database for customer: {}", customerId);

        } catch (Exception e) {
            log.error("Failed to sync cart to database for customer: {}", customerId, e);
            throw new RuntimeException("카트 데이터베이스 동기화에 실패했습니다.", e);
        }
    }

    private void clearCartFromDatabase(UUID customerId) {
        try {
            cartRepository.deleteByCustomerId(customerId);
            log.debug("Cart cleared from database for customer: {}", customerId);
        } catch (Exception e) {
            log.error("Failed to clear cart from database for customer: {}", customerId, e);
            throw new RuntimeException("데이터베이스 카트 삭제에 실패했습니다.", e);
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
}