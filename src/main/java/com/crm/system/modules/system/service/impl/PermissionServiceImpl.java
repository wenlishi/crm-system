package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.system.entity.Permission;
import com.crm.system.modules.system.mapper.PermissionMapper;
import com.crm.system.modules.system.mapper.RolePermissionMapper;
import com.crm.system.modules.system.mapper.UserRoleMapper;
import com.crm.system.modules.system.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Page<Permission> pagePermissions(Integer current, Integer size) {
        Page<Permission> page = new Page<>(current, size);
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        return page(page, wrapper);
    }

    @Override
    public List<Permission> listAvailablePermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder);
        return list(wrapper);
    }

    @Override
    public List<Permission> listByRoleId(Long roleId) {
        return rolePermissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<Permission> listByUserId(Long userId) {
        // 查询用户的所有角色
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询所有角色的权限
        List<Permission> allPermissions = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<Permission> permissions = rolePermissionMapper.selectPermissionsByRoleId(roleId);
            allPermissions.addAll(permissions);
        }
        
        // 去重
        return allPermissions.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> listTree() {
        List<Permission> allPermissions = listAvailablePermissions();
        List<Permission> tree = new ArrayList<>();
        
        // 找到所有一级菜单（parentId=0 或 null）
        for (Permission permission : allPermissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                tree.add(permission);
            }
        }
        
        // 递归构建子菜单
        for (Permission node : tree) {
            node.setChildren(findChildren(node.getPermissionId(), allPermissions));
        }
        
        return tree;
    }

    /**
     * 递归查找子节点
     */
    private List<Permission> findChildren(Long parentId, List<Permission> allPermissions) {
        List<Permission> children = new ArrayList<>();
        for (Permission permission : allPermissions) {
            if (parentId.equals(permission.getParentId())) {
                children.add(permission);
                // 递归查找子节点的子节点
                permission.setChildren(findChildren(permission.getPermissionId(), allPermissions));
            }
        }
        return children;
    }
}
