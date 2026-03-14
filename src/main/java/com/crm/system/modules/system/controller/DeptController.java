package com.crm.system.modules.system.controller;

import com.crm.system.common.Result;
import com.crm.system.common.annotation.OperationLog;
import com.crm.system.common.annotation.RequirePermission;
import com.crm.system.modules.system.entity.Dept;
import com.crm.system.modules.system.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/depts")
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 查询部门树形结构
     */
    @GetMapping("/tree")
    @RequirePermission("system:dept:query")
    @OperationLog(module = "系统管理", type = "查询", description = "查询部门树")
    public Result<List<Dept>> tree() {
        List<Dept> tree = deptService.listTree();
        return Result.success(tree);
    }

    /**
     * 查询部门列表
     */
    @GetMapping("/list")
    @RequirePermission("system:dept:query")
    @OperationLog(module = "系统管理", type = "查询", description = "查询部门列表")
    public Result<List<Dept>> list() {
        List<Dept> list = deptService.list();
        return Result.success(list);
    }

    /**
     * 查询部门详情
     */
    @GetMapping("/{id}")
    @RequirePermission("system:dept:query")
    public Result<Dept> getById(@PathVariable Long id) {
        Dept dept = deptService.getById(id);
        if (dept == null) {
            return Result.error("部门不存在");
        }
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @PostMapping
    @RequirePermission("system:dept:add")
    @OperationLog(module = "系统管理", type = "新增", description = "新增部门")
    public Result<Dept> add(@RequestBody Dept dept) {
        deptService.save(dept);
        return Result.success(dept);
    }

    /**
     * 更新部门
     */
    @PutMapping
    @RequirePermission("system:dept:edit")
    @OperationLog(module = "系统管理", type = "修改", description = "修改部门信息")
    public Result<Dept> update(@RequestBody Dept dept) {
        deptService.updateById(dept);
        return Result.success(dept);
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @RequirePermission("system:dept:delete")
    @OperationLog(module = "系统管理", type = "删除", description = "删除部门")
    public Result<Void> delete(@PathVariable Long id) {
        // 检查是否有子部门
        List<Dept> children = deptService.listChildren(id);
        if (children != null && !children.isEmpty()) {
            return Result.error("请先删除子部门");
        }
        
        deptService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 获取部门统计
     */
    @GetMapping("/stats")
    @RequirePermission("system:dept:query")
    @OperationLog(module = "系统管理", type = "查询", description = "查询部门统计")
    public Result<DeptService.DeptStats> stats() {
        DeptService.DeptStats stats = deptService.getStats();
        return Result.success(stats);
    }
}
