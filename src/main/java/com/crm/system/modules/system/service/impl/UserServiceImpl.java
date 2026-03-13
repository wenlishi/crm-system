package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.common.exception.BusinessException;
import com.crm.system.common.utils.JwtUtil;
import com.crm.system.modules.system.dto.LoginResponse;
import com.crm.system.modules.system.entity.User;
import com.crm.system.modules.system.mapper.UserMapper;
import com.crm.system.modules.system.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(String username, String password) {
        // 1. 查询用户
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 4. 生成 Token
        String token = JwtUtil.generateToken(user.getUserId(), user.getUsername());

        return new LoginResponse(
            token,
            user.getUserId(),
            user.getUsername(),
            System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L
        );
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return this.getOne(wrapper);
    }
}
