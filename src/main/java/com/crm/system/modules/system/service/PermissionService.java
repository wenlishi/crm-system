package com.crm.system.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.system.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 权限服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 分页查询权限
     * 
     * @param current 当前页码
     * @param size 每页数量
     * @return 分页结果
     */
    Page<Permission> pagePermissions(Integer current, Integer size);

    /**
     * 查询所有可用权限
     * 
     * @return 权限列表
     */
    List<Permission> listAvailablePermissions();

    /**
     * 根据角色 ID 查询权限列表
     * 
     * @param roleId 角色 ID
     * @return 权限列表
     */
    List<Permission> listByRoleId(Long roleId);

    /**
     * 根据用户 ID 查询权限列表
     * 
     * @param userId 用户 ID
     * @return 权限列表
     */
    List<Permission> listByUserId(Long userId);

    /**
     * 查询树形权限列表
     * 
     * @return 树形权限列表
     */
    List<Permission> listTree();
}
