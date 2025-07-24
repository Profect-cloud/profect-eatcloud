package profect.eatcloud.Domain.Admin.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/admin/stores")
public class AdminStoreController {
    @PostMapping
    public void addStore(Authentication authentication) {}

    @GetMapping
    public void getStores(Authentication authentication) {}

    @PatchMapping("/{store_id}")
    public void updateStore(Authentication authentication, @PathVariable String store_id) {}

    @DeleteMapping("/{store_id}")
    public void deleteStore(Authentication authentication, @PathVariable String store_id) {}
} 