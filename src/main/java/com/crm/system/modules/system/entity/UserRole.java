package com.crm.system.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@TableName("sys_user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    /**
     * 角色 ID
     */
    @TableId(value = "role_id", type = IdType.INPUT)
    private Long roleId;
}
