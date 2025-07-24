package profect.eatcloud.Domain.Admin.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    @PostMapping
    public void addCategory(Authentication authentication) {}

    @PatchMapping("/{category_id}")
    public void updateCategory(Authentication authentication, @PathVariable String category_id) {}

    @DeleteMapping("/{category_id}")
    public void deleteCategory(Authentication authentication, @PathVariable String category_id) {}
} 