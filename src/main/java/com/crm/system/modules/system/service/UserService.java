package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.system.modules.system.dto.LoginResponse;
import com.crm.system.modules.system.entity.User;

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
}
