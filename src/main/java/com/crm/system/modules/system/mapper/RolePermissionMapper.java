package com.crm.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<com.crm.system.modules.system.entity.RolePermission> {

    /**
     * 根据角色 ID 查询权限列表
     * 
     * @param roleId 角色 ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.deleted = 0")
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);
}
