package com.murali.placify.security;

import com.murali.placify.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoDetails implements UserDetails {
    private final String email;
    private final String password;
    private final List<GrantedAuthority> authorityList;
    private final boolean enabled;
    public UserInfoDetails(User user){
        email = user.getMailID();
        password = user.getPassword();
        authorityList = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        enabled = user.isEnabled();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return enabled;
    }
}


