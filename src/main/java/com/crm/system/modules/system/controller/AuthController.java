package com.crm.system.modules.system.controller;

import com.crm.system.common.Result;
import com.crm.system.modules.system.dto.LoginRequest;
import com.crm.system.modules.system.dto.LoginResponse;
import com.crm.system.modules.system.entity.User;
import com.crm.system.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户 Controller
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getUsername(), request.getPassword());
        return Result.success(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getInfo() {
        // TODO: 从 Token 中解析用户 ID
        User user = userService.getById(1L);
        return Result.success(user);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody LoginRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        // TODO: 密码加密
        user.setPassword(request.getPassword());
        user.setStatus(1);
        
        boolean success = userService.save(user);
        return success ? Result.success("注册成功", true) : Result.error("注册失败");
    }
}
