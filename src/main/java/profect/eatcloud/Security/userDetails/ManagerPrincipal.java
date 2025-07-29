package profect.eatcloud.Security.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import profect.eatcloud.Domain.Manager.Entity.Manager;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class ManagerPrincipal implements UserDetails {
    private final Manager manager;
    public ManagerPrincipal(Manager manager) {
        this.manager = manager;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_MANAGER"));
    }
    @Override
    public String getUsername() {
        return manager.getEmail();
    }
    @Override
    public String getPassword() {
        return manager.getPassword();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked()  { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public UUID getId() { return manager.getId(); }
}
