package com.chen.LeoBlog.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Slf4j

public class LoginUser implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 144441L;

    private User user;
    private List<String> permissions;
    private transient volatile List<GrantedAuthority> authorities;


    public LoginUser(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if (authorities == null) {
//            synchronized (this) {
//                if (authorities == null) {
//                    authorities = this.permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
//                }
//            }
//        }
        return Collections.EMPTY_LIST;
//        return authorities;
    }

    @Override
    public String getPassword() {
        return "{noop}" + user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
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
