package com.crm.system.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.system.dto.UserRequest;
import com.crm.system.modules.system.entity.User;
import com.crm.system.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

/**
 * 用户管理 Controller
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping("/page")
    public Result<Page<User>> page(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<User> page = userService.pageQuery(username, phone, deptId, status, current, size);
        return Result.success(page);
    }

    /**
     * 查询所有用户（简单列表）
     */
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    /**
     * 根据 ID 查询用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Boolean> save(@Valid @RequestBody UserRequest request) {
        // 检查用户名是否已存在
        User existUser = userService.getByUsername(request.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // Service 层会加密
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setDeptId(request.getDeptId());
        user.setStatus(request.getStatus() != null ? request.getStatus() : 1);

        boolean success = userService.saveUser(user);
        return success ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Boolean> update(@Valid @RequestBody UserRequest request) {
        if (request.getUserId() == null) {
            return Result.error("用户 ID 不能为空");
        }

        User existUser = userService.getById(request.getUserId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }

        // 如果修改了用户名，检查是否与其他用户重复
        if (!existUser.getUsername().equals(request.getUsername())) {
            User usernameExist = userService.getByUsername(request.getUsername());
            if (usernameExist != null && !usernameExist.getUserId().equals(request.getUserId())) {
                return Result.error("用户名已存在");
            }
        }

        User user = new User();
        user.setUserId(request.getUserId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setDeptId(request.getDeptId());
        user.setStatus(request.getStatus());

        boolean success = userService.updateById(user);
        return success ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 不允许删除自己
        Long currentUserId = 1L; // TODO: 从 Token 中获取
        if (id.equals(currentUserId)) {
            return Result.error("不能删除自己");
        }

        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping
    public Result<Boolean> batchDelete(@RequestParam List<Long> ids) {
        // 不允许删除自己
        Long currentUserId = 1L; // TODO: 从 Token 中获取
        if (ids.contains(currentUserId)) {
            return Result.error("不能删除自己");
        }

        boolean success = userService.removeByIds(ids);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success ? Result.success("状态更新成功", true) : Result.error("更新失败");
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/password/reset")
    public Result<Boolean> resetPassword(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 重置为默认密码 123456
        String defaultPassword = "123456";
        user.setPassword(defaultPassword); // Service 层会加密
        
        boolean success = userService.updateById(user);
        return success ? Result.success("密码已重置为 123456", true) : Result.error("重置失败");
    }

    /**
     * 修改自己的密码
     */
    @PutMapping("/password")
    public Result<Boolean> updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        // TODO: 从 Token 中获取当前用户 ID
        Long userId = 1L;
        
        boolean success = userService.updatePassword(userId, oldPassword, newPassword);
        return success ? Result.success("密码修改成功", true) : Result.error("原密码错误");
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    public Result<Boolean> assignRoles(
            @PathVariable Long id,
            @RequestParam List<Long> roleIds) {
        
        boolean success = userService.assignRoles(id, roleIds);
        return success ? Result.success("角色分配成功", true) : Result.error("分配失败");
    }

    /**
     * 查询用户的角色 ID 列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        List<Long> roleIds = userService.getUserRoleIds(id);
        return Result.success(roleIds);
    }
}
