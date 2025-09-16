package com.leo.cathay.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserAccountProvider {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}