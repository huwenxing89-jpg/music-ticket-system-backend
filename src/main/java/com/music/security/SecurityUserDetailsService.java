package com.music.security;

import com.music.modules.user.entity.User;
import com.music.modules.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Security用户详情服务
 *
 * @author 黄晓倩
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.error("用户不存在：{}", username);
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 检查用户状态：1-禁用，0-正常
        if (user.getStatus() == 1) {
            log.error("用户已被禁用：{}", username);
            throw new UsernameNotFoundException("用户已被禁用：" + username);
        }

        log.debug("加载用户信息：{}", username);
        return new SecurityUserDetails(user);
    }
}
