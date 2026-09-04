package com.example.chatConnectSpring.shared.security;

import com.example.chatConnectSpring.user.domain.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {
    
    private final UUID id;
    private final String clerkUserId;
    private final String username;
    
    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.clerkUserId = user.getClerkUserId();
        this.username = user.getEmail();
    }
    
    public UUID getId() {
        return id;
    }

    public String getClerkUserId() {
        return clerkUserId;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
    
    @Override
    public @Nullable String getPassword() {
        return "";
    }
    
    @Override
    public String getUsername() {
        return username;
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
