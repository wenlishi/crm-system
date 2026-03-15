package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.system.dto.LoginResponse;
import com.crm.system.modules.system.entity.User;

import java.util.List;

/**
 * 用户 Service 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    LoginResponse login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 分页查询用户
     */
    Page<User> pageQuery(String username, String phone, Long deptId, Integer status, 
                         Integer current, Integer size);

    /**
     * 保存用户（加密密码）
     */
    boolean saveUser(User user);

    /**
     * 更新用户密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 为用户分配角色
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色 ID 列表
     */
    List<Long> getUserRoleIds(Long userId);
}
