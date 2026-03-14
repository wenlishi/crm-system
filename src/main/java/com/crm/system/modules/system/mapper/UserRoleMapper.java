package com.crm.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.modules.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联 Mapper 接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<com.crm.system.modules.system.entity.UserRole> {

    /**
     * 根据用户 ID 查询角色列表
     * 
     * @param userId 用户 ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<Role> selectRolesByUserId(Long userId);

    /**
     * 根据用户 ID 查询角色 ID 列表
     * 
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(Long userId);
}
