package com.crm.system.modules.statistics.controller;

import com.crm.system.common.Result;
import com.crm.system.modules.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 统计 Controller
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取首页统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = statisticsService.getDashboardStats();
        return Result.success(stats);
    }

    /**
     * 获取客户增长统计
     */
    @GetMapping("/customer-growth")
    public Result<Map<String, Object>> getCustomerGrowthStats() {
        Map<String, Object> stats = statisticsService.getCustomerGrowthStats();
        return Result.success(stats);
    }

    /**
     * 获取跟进记录统计
     */
    @GetMapping("/follow-up")
    public Result<Map<String, Object>> getFollowUpStats() {
        Map<String, Object> stats = statisticsService.getFollowUpStats();
        return Result.success(stats);
    }
}
