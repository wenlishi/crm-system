package com.crm.system.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.system.entity.Permission;
import com.crm.system.modules.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页查询权限
     */
    @GetMapping("/page")
    public Result<Page<Permission>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Permission> page = permissionService.pagePermissions(current, size);
        return Result.success(page);
    }

    /**
     * 查询所有可用权限
     */
    @GetMapping("/list")
    public Result<List<Permission>> list() {
        List<Permission> list = permissionService.listAvailablePermissions();
        return Result.success(list);
    }

    /**
     * 查询权限树
     */
    @GetMapping("/tree")
    public Result<List<Permission>> tree() {
        List<Permission> tree = permissionService.listTree();
        return Result.success(tree);
    }

    /**
     * 查询权限详情
     */
    @GetMapping("/{id}")
    public Result<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    /**
     * 新增权限
     */
    @PostMapping
    public Result<Permission> add(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success(permission);
    }

    /**
     * 更新权限
     */
    @PutMapping
    public Result<Permission> update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.success(permission);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return Result.success();
    }

    /**
     * 查询角色的权限列表
     */
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> listByRole(@PathVariable Long roleId) {
        List<Permission> list = permissionService.listByRoleId(roleId);
        return Result.success(list);
    }

    /**
     * 查询用户的权限列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> listByUser(@PathVariable Long userId) {
        List<Permission> list = permissionService.listByUserId(userId);
        return Result.success(list);
    }
}
