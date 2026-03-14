package com.crm.system.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.system.modules.system.entity.Dept;
import com.crm.system.modules.system.mapper.DeptMapper;
import com.crm.system.modules.system.service.DeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<Dept> listTree() {
        // 查询所有部门
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Dept::getSortOrder);
        List<Dept> allDepts = list(wrapper);

        // 构建部门树
        List<Dept> tree = new ArrayList<>();
        
        // 找到所有一级部门（parentId=0 或 null）
        for (Dept dept : allDepts) {
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                tree.add(dept);
            }
        }
        
        // 递归构建子部门
        for (Dept node : tree) {
            node.setChildren(findChildren(node.getDeptId(), allDepts));
        }
        
        return tree;
    }

    @Override
    public List<Dept> listChildren(Long parentId) {
        LambdaQueryWrapper<Dept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dept::getParentId, parentId)
                .orderByAsc(Dept::getSortOrder);
        return list(wrapper);
    }

    @Override
    public DeptStats getStats() {
        DeptStats stats = new DeptStats();
        
        // 部门总数
        Long total = count();
        stats.setTotal(total.intValue());
        
        // 可用部门数
        LambdaQueryWrapper<Dept> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Dept::getStatus, 1);
        Long active = count(activeWrapper);
        stats.setActive(active.intValue());
        
        // 禁用部门数
        LambdaQueryWrapper<Dept> disabledWrapper = new LambdaQueryWrapper<>();
        disabledWrapper.eq(Dept::getStatus, 0);
        Long disabled = count(disabledWrapper);
        stats.setDisabled(disabled.intValue());
        
        return stats;
    }

    /**
     * 递归查找子部门
     */
    private List<Dept> findChildren(Long parentId, List<Dept> allDepts) {
        List<Dept> children = new ArrayList<>();
        for (Dept dept : allDepts) {
            if (parentId.equals(dept.getParentId())) {
                children.add(dept);
                // 递归查找子部门的子部门
                dept.setChildren(findChildren(dept.getDeptId(), allDepts));
            }
        }
        return children;
    }
}
