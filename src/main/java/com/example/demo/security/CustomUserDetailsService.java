package com.example.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 用户详情服务接口
 */
public interface CustomUserDetailsService extends UserDetailsService {

    /**
     * 根据用户名加载用户详情
     */
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
