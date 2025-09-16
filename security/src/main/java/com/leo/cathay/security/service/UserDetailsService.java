package com.leo.cathay.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserAccountProvider userAccountProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 依賴 UserAccountProvider 介面來取得使用者詳細資訊
        return userAccountProvider.loadUserByUsername(username);
    }
}