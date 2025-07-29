package profect.eatcloud.Security.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import profect.eatcloud.Domain.Customer.Entity.Customer;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class CustomerPrincipal implements UserDetails {
    private final Customer customer;
    public CustomerPrincipal(Customer customer) {
        this.customer = customer;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }
    @Override
    public String getUsername() {
        return customer.getEmail();
    }
    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked()  { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public UUID getId() { return customer.getId(); }
}
