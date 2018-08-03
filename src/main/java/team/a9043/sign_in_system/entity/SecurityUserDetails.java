package team.a9043.sign_in_system.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SecurityUserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private SisUser sisUser;

    public SecurityUserDetails(SisUser sisUser) {
        this.sisUser = sisUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return sisUser.getSuEnPassword();
    }

    @Override
    public String getUsername() {
        return sisUser.getSuId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
