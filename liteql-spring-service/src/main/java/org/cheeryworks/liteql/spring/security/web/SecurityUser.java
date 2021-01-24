package org.cheeryworks.liteql.spring.security.web;

import org.cheeryworks.liteql.skeleton.model.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;

public class SecurityUser implements UserDetails, UserType {

    private UserDetails userDetails;

    private UserType user;

    public SecurityUser(UserType user) {
        Assert.notNull(user, "UserEntity is required");

        this.user = user;
    }

    public SecurityUser(UserDetails userDetails) {
        Assert.notNull(userDetails, "UserDetails is required");

        this.userDetails = userDetails;
    }

    @Override
    public String getId() {
        return this.user.getId();
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

    @Override
    public String getAvatarUrl() {
        return this.user.getAvatarUrl();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userDetails.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.userDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.userDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.userDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.userDetails.isEnabled();
    }

}
