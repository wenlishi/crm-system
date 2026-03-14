package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.modules.system.entity.Role;
import com.crm.system.modules.system.mapper.RoleMapper;
import com.crm.system.modules.system.mapper.UserRoleMapper;
import com.crm.system.modules.system.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Page<Role> pageRoles(Integer current, Integer size) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<Role> listAvailableRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1)
                .orderByAsc(Role::getRoleCode);
        return list(wrapper);
    }

    @Override
    public List<Role> listByUserId(Long userId) {
        return userRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public RoleStats getStats() {
        RoleStats stats = new RoleStats();
        
        // 角色总数
        Long total = count();
        stats.setTotal(total.intValue());
        
        // 可用角色数
        LambdaQueryWrapper<Role> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Role::getStatus, 1);
        Long active = count(activeWrapper);
        stats.setActive(active.intValue());
        
        // 禁用角色数
        LambdaQueryWrapper<Role> disabledWrapper = new LambdaQueryWrapper<>();
        disabledWrapper.eq(Role::getStatus, 0);
        Long disabled = count(disabledWrapper);
        stats.setDisabled(disabled.intValue());
        
        return stats;
    }
}
