package com.crm.system.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关联实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@TableName("sys_role_permission")
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色 ID
     */
    @TableId(value = "role_id", type = IdType.INPUT)
    private Long roleId;

    /**
     * 权限 ID
     */
    @TableId(value = "permission_id", type = IdType.INPUT)
    private Long permissionId;
}
