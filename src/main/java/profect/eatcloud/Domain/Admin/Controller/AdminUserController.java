package profect.eatcloud.Domain.Admin.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
    @GetMapping
    public void getUsers(Authentication authentication) {}

    @DeleteMapping("/{user_id}")
    public void deleteUser(Authentication authentication, @PathVariable String user_id) {}
} 