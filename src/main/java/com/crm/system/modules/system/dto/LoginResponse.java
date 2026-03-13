package com.crm.system.modules.system.dto;

import lombok.Data;

/**
 * 登录响应 DTO
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
public class LoginResponse {

    /**
     * Token
     */
    private String token;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 过期时间
     */
    private Long expiration;

    public LoginResponse(String token, Long userId, String username, Long expiration) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.expiration = expiration;
    }
}
