package profect.eatcloud.Security.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import profect.eatcloud.Domain.Admin.Entity.Admin;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class AdminPrincipal implements UserDetails {
    private final Admin admin;
    public AdminPrincipal(Admin admin) {
        this.admin = admin;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    @Override
    public String getUsername() {
        return admin.getEmail();
    }
    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked()  { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public UUID getId() { return admin.getId(); }
}
