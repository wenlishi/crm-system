package com.crm.system.modules.customer.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.common.annotation.OperationLog;
import com.crm.system.common.annotation.RequirePermission;
import com.crm.system.common.utils.UserContext;
import com.crm.system.modules.customer.entity.CustomerPool;
import com.crm.system.modules.customer.service.CustomerPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公海池控制器
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/customer-pool")
public class CustomerPoolController {

    @Autowired
    private CustomerPoolService customerPoolService;

    /**
     * 分页查询公海池客户
     */
    @GetMapping("/page")
    @RequirePermission("customer:pool:query")
    @OperationLog(module = "公海池", type = "查询", description = "查询公海池客户")
    public Result<Page<CustomerPool>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CustomerPool> page = customerPoolService.pagePool(current, size);
        return Result.success(page);
    }

    /**
     * 查询公海池统计
     */
    @GetMapping("/stats")
    @RequirePermission("customer:pool:query")
    @OperationLog(module = "公海池", type = "查询", description = "查询公海池统计")
    public Result<CustomerPoolService.PoolStats> stats() {
        CustomerPoolService.PoolStats stats = customerPoolService.getStats();
        return Result.success(stats);
    }

    /**
     * 领取公海池客户
     */
    @PostMapping("/claim/{poolId}")
    @RequirePermission("customer:pool:claim")
    @OperationLog(module = "公海池", type = "领取", description = "领取公海池客户")
    public Result<Boolean> claim(@PathVariable Long poolId) {
        Long userId = UserContext.getUserId();
        boolean success = customerPoolService.claimCustomer(poolId, userId);
        return success ? Result.success("领取成功", true) : Result.error("领取失败");
    }

    /**
     * 分配公海池客户（管理员）
     */
    @PostMapping("/assign/{poolId}")
    @RequirePermission("customer:pool:assign")
    @OperationLog(module = "公海池", type = "分配", description = "分配公海池客户")
    public Result<Boolean> assign(
            @PathVariable Long poolId,
            @RequestParam Long userId) {
        boolean success = customerPoolService.assignCustomer(poolId, userId);
        return success ? Result.success("分配成功", true) : Result.error("分配失败");
    }

    /**
     * 手动释放客户到公海池
     */
    @PostMapping("/drop/{customerId}")
    @RequirePermission("customer:pool:drop")
    @OperationLog(module = "公海池", type = "释放", description = "手动释放客户到公海池")
    public Result<Void> drop(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer reason,
            @RequestParam(required = false) String remark) {
        customerPoolService.dropToPool(customerId, reason != null ? reason : 4, remark);
        return Result.success("释放成功");
    }
}
