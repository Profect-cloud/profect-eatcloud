package profect.eatcloud.Domain.User.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage/addresses")
public class AddressController {
    @GetMapping
    public void getAddresses(org.springframework.security.core.Authentication authentication) {}

    @PostMapping
    public void addAddress(org.springframework.security.core.Authentication authentication) {}

    @PatchMapping("/{address_id}")
    public void updateAddress(org.springframework.security.core.Authentication authentication, @PathVariable String address_id) {}

    @DeleteMapping("/{address_id}")
    public void deleteAddress(org.springframework.security.core.Authentication authentication, @PathVariable String address_id) {}

    @PatchMapping("/{address_id}/select")
    public void selectAddress(org.springframework.security.core.Authentication authentication, @PathVariable String address_id) {}
} 