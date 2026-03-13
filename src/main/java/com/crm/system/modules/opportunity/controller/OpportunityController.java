package com.crm.system.modules.opportunity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.system.common.Result;
import com.crm.system.modules.opportunity.entity.Opportunity;
import com.crm.system.modules.opportunity.service.OpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 商机 Controller
 */
@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    @Autowired
    private OpportunityService opportunityService;

    /**
     * 查询客户的商机列表
     */
    @GetMapping("/customer/{customerId}")
    public Result<List<Opportunity>> listByCustomerId(@PathVariable Long customerId) {
        List<Opportunity> list = opportunityService.listByCustomerId(customerId);
        return Result.success(list);
    }

    /**
     * 分页查询商机（按阶段）
     */
    @GetMapping("/page")
    public Result<Page<Opportunity>> page(
            @RequestParam(required = false) Integer stage,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Opportunity> page = opportunityService.pageByStage(stage, current, size);
        return Result.success(page);
    }

    /**
     * 根据 ID 查询商机
     */
    @GetMapping("/{id}")
    public Result<Opportunity> getById(@PathVariable Long id) {
        Opportunity opportunity = opportunityService.getById(id);
        if (opportunity == null) {
            return Result.error("商机不存在");
        }
        return Result.success(opportunity);
    }

    /**
     * 添加商机
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody Opportunity opportunity) {
        boolean success = opportunityService.save(opportunity);
        return success ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    /**
     * 更新商机
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Opportunity opportunity) {
        boolean success = opportunityService.updateById(opportunity);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    /**
     * 删除商机
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean success = opportunityService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    /**
     * 获取阶段统计
     */
    @GetMapping("/stats/stage")
    public Result<Map<String, Object>> getStageStats() {
        Map<String, Object> stats = opportunityService.getStageStats();
        return Result.success(stats);
    }
}
