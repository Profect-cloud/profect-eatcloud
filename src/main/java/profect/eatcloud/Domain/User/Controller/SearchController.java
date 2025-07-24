package profect.eatcloud.Domain.User.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/stores/{storeId}/menus")
public class SearchController {
    @GetMapping
    public void getMenus(Authentication authentication, @PathVariable String storeId) {}

    @GetMapping("/category")
    public void searchByCategory(Authentication authentication) {}

    @GetMapping("/menu-category")
    public void searchByMenuCategory(Authentication authentication) {}
} 