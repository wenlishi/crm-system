package com.crm.system.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.system.entity.OperationLog;
import com.crm.system.modules.system.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping("/page")
    public Result<Page<OperationLog>> page(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<OperationLog> page = operationLogService.pageLogs(module, type, userId, current, size);
        return Result.success(page);
    }

    /**
     * 查询操作日志详情
     */
    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        if (log == null) {
            return Result.error("日志不存在");
        }
        return Result.success(log);
    }

    /**
     * 清空操作日志
     */
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        operationLogService.clearLogs();
        return Result.success("日志已清空");
    }

    /**
     * 按模块查询统计
     */
    @GetMapping("/stats/module")
    public Result<Object> statsByModule() {
        // TODO: 实现按模块统计
        return Result.success("待实现");
    }

    /**
     * 按用户查询统计
     */
    @GetMapping("/stats/user")
    public Result<Object> statsByUser() {
        // TODO: 实现按用户统计
        return Result.success("待实现");
    }
}
