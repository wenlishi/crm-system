package com.crm.system.modules.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户请求 DTO
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
@Data
public class UserRequest {

    /**
     * 用户 ID（修改时必填）
     */
    private Long userId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须是 4-20 位字母、数字或下划线")
    private String username;

    /**
     * 密码（新增时必填）
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密码必须是 6-20 位字母、数字或下划线")
    private String password;

    /**
     * 邮箱
     */
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 状态（0 禁用 1 正常）
     */
    private Integer status;
}
