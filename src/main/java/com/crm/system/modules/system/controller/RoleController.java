package com.crm.system.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.system.entity.Role;
import com.crm.system.modules.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色
     */
    @GetMapping("/page")
    public Result<Page<Role>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Role> page = roleService.pageRoles(current, size);
        return Result.success(page);
    }

    /**
     * 查询所有可用角色
     */
    @GetMapping("/list")
    public Result<List<Role>> list() {
        List<Role> list = roleService.listAvailableRoles();
        return Result.success(list);
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<Role> add(@RequestBody Role role) {
        roleService.save(role);
        return Result.success(role);
    }

    /**
     * 更新角色
     */
    @PutMapping
    public Result<Role> update(@RequestBody Role role) {
        roleService.updateById(role);
        return Result.success(role);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success();
    }

    /**
     * 获取角色统计
     */
    @GetMapping("/stats")
    public Result<RoleService.RoleStats> getStats() {
        RoleService.RoleStats stats = roleService.getStats();
        return Result.success(stats);
    }

    /**
     * 更新角色状态
     */
    @PutMapping("/{id}/status")
    public Result<Role> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        role.setStatus(status);
        roleService.updateById(role);
        return Result.success(role);
    }

    /**
     * 查询用户的角色列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Role>> listByUser(@PathVariable Long userId) {
        List<Role> list = roleService.listByUserId(userId);
        return Result.success(list);
    }
}
