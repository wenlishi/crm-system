package com.crm.system.common.utils;

import com.crm.system.common.annotation.DataType;
import com.crm.system.modules.system.entity.Role;
import com.crm.system.modules.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据权限计算器
 * 
 * 根据用户角色计算数据权限范围
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
public class DataPermissionCalculator {

    @Autowired
    private RoleService roleService;

    /**
     * 计算数据权限 SQL 过滤条件
     * 
     * @param userId 用户 ID
     * @param dataType 数据类型
     * @param fieldName 字段名（如 owner_id）
     * @return SQL 过滤条件
     */
    public String calculatePermissionFilter(Long userId, DataType dataType, String fieldName) {
        if (userId == null) {
            return "1=0"; // 无权限
        }

        // 超级管理员（userId=1）查看所有数据
        if (userId == 1L) {
            return "1=1";
        }

        // 获取用户的角色列表
        List<Role> roles = roleService.listByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return "1=0"; // 无角色，无权限
        }

        // 检查是否有超级管理员角色
        boolean hasSuperAdmin = roles.stream()
                .anyMatch(r -> "super_admin".equals(r.getRoleCode()));
        
        if (hasSuperAdmin) {
            return "1=1"; // 超级管理员查看所有数据
        }

        // 检查是否有销售经理角色
        boolean hasSalesManager = roles.stream()
                .anyMatch(r -> "sales_manager".equals(r.getRoleCode()));
        
        if (hasSalesManager) {
            // 经理可以查看自己和下属的数据（简化为查看所有）
            return "1=1";
        }

        // 销售专员：只能查看自己的数据
        return fieldName + " = " + userId;
    }

    /**
     * 计算客户数据权限
     */
    public String calculateCustomerPermission(Long userId) {
        return calculatePermissionFilter(userId, DataType.CUSTOMER, "owner_id");
    }

    /**
     * 计算商机数据权限
     */
    public String calculateOpportunityPermission(Long userId) {
        return calculatePermissionFilter(userId, DataType.OPPORTUNITY, "owner_id");
    }

    /**
     * 计算合同数据权限
     */
    public String calculateContractPermission(Long userId) {
        return calculatePermissionFilter(userId, DataType.CONTRACT, "owner_id");
    }

    /**
     * 计算跟进记录数据权限
     */
    public String calculateFollowUpPermission(Long userId) {
        // 跟进记录通过 customer_id 间接关联 owner_id
        return calculatePermissionFilter(userId, DataType.FOLLOW_UP, "customer_id");
    }
}
