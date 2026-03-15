package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.system.common.exception.BusinessException;
import com.crm.system.common.utils.JwtUtil;
import com.crm.system.modules.system.dto.LoginResponse;
import com.crm.system.modules.system.entity.User;
import com.crm.system.modules.system.entity.UserRole;
import com.crm.system.modules.system.mapper.UserMapper;
import com.crm.system.modules.system.mapper.UserRoleMapper;
import com.crm.system.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 Service 实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRoleMapper userRoleMapper;

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

    @Override
    public Page<User> pageQuery(String username, String phone, Long deptId, Integer status, 
                                Integer current, Integer size) {
        Page<User> page = new Page<>(current, size);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), User::getUsername, username)
               .like(StringUtils.hasText(phone), User::getPhone, phone)
               .eq(deptId != null, User::getDeptId, deptId)
               .eq(status != null, User::getStatus, status)
               .orderByDesc(User::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public boolean saveUser(User user) {
        // 密码加密
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return this.save(user);
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(user);
    }

    @Override
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 1. 删除用户原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 2. 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                .map(roleId -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    return userRole;
                })
                .collect(Collectors.toList());
            
            // 批量插入
            for (UserRole userRole : userRoles) {
                userRoleMapper.insert(userRole);
            }
        }

        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        return userRoles.stream()
            .map(UserRole::getRoleId)
            .collect(Collectors.toList());
    }
}
