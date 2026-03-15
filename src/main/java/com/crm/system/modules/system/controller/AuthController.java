package com.crm.system.modules.system.controller;

import com.crm.system.common.Result;
import com.crm.system.common.interceptor.CurrentUserContext;
import com.crm.system.modules.system.dto.LoginRequest;
import com.crm.system.modules.system.dto.LoginResponse;
import com.crm.system.modules.system.entity.User;
import com.crm.system.modules.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证 Controller
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Tag(name = "认证管理", description = "用户登录、注册、信息查询")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录", description = "用户名密码登录，返回 JWT Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getUsername(), request.getPassword());
        return Result.success(response);
    }

    @Operation(summary = "获取当前用户信息", description = "从 Token 中解析用户 ID，返回用户详细信息")
    @GetMapping("/info")
    public Result<User> getInfo() {
        Long userId = CurrentUserContext.getUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "用户注册", description = "注册新用户，密码自动 BCrypt 加密")
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody LoginRequest request) {
        User existUser = userService.getByUsername(request.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(1);
        
        boolean success = userService.saveUser(user);
        return success ? Result.success("注册成功", true) : Result.error("注册失败");
    }
}
