package profect.eatcloud.Domain.Admin.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {
    @GetMapping
    public void getDashboard(Authentication authentication) {}
} 