package com.crm.system.common.utils;

import com.crm.system.modules.system.entity.Permission;
import com.crm.system.modules.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验工具类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
public class PermissionChecker {

    @Autowired
    private PermissionService permissionService;

    /**
     * 检查用户是否有指定权限
     * 
     * @param userId 用户 ID
     * @param permissionCode 权限编码
     * @return true=有权限，false=无权限
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        if (userId == null || permissionCode == null || permissionCode.isEmpty()) {
            return false;
        }

        // 超级管理员拥有所有权限（用户 ID=1 默认为超级管理员）
        if (userId == 1L) {
            return true;
        }

        // 查询用户的所有权限
        List<Permission> permissions = permissionService.listByUserId(userId);
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        // 检查是否包含指定权限
        Set<String> permissionCodes = permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());

        return permissionCodes.contains(permissionCode);
    }

    /**
     * 检查用户是否有任一权限
     * 
     * @param userId 用户 ID
     * @param permissionCodes 权限编码列表
     * @return true=有任一权限，false=无任何权限
     */
    public boolean hasAnyPermission(Long userId, String... permissionCodes) {
        if (userId == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }

        // 超级管理员拥有所有权限
        if (userId == 1L) {
            return true;
        }

        for (String code : permissionCodes) {
            if (hasPermission(userId, code)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查用户是否有所有权限
     * 
     * @param userId 用户 ID
     * @param permissionCodes 权限编码列表
     * @return true=有所有权限，false=缺少任一权限
     */
    public boolean hasAllPermissions(Long userId, String... permissionCodes) {
        if (userId == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }

        // 超级管理员拥有所有权限
        if (userId == 1L) {
            return true;
        }

        for (String code : permissionCodes) {
            if (!hasPermission(userId, code)) {
                return false;
            }
        }

        return true;
    }
}
