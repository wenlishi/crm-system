package com.crm.system.modules.statistics.controller;

import com.crm.system.common.Result;
import com.crm.system.common.annotation.OperationLog;
import com.crm.system.common.annotation.RequirePermission;
import com.crm.system.modules.statistics.dto.*;
import com.crm.system.modules.statistics.service.StatisticsAdvancedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计分析控制器（增强版）
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsAdvancedController {

    @Autowired
    private StatisticsAdvancedService statisticsAdvancedService;

    /**
     * 获取销售排行榜
     */
    @GetMapping("/sales-ranking")
    @RequirePermission("statistics:sales-ranking")
    @OperationLog(module = "数据统计", type = "查询", description = "查询销售排行榜")
    public Result<List<SalesRankingDTO>> salesRanking(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusMonths(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        List<SalesRankingDTO> ranking = statisticsAdvancedService.getSalesRanking(startTime, endTime, limit);
        return Result.success(ranking);
    }

    /**
     * 获取转化率分析
     */
    @GetMapping("/conversion-rate")
    @RequirePermission("statistics:conversion")
    @OperationLog(module = "数据统计", type = "查询", description = "查询转化率分析")
    public Result<ConversionRateDTO> conversionRate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusMonths(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        ConversionRateDTO dto = statisticsAdvancedService.getConversionRate(startTime, endTime);
        return Result.success(dto);
    }

    /**
     * 获取客户来源分析
     */
    @GetMapping("/customer-source")
    @RequirePermission("statistics:source")
    @OperationLog(module = "数据统计", type = "查询", description = "查询客户来源分析")
    public Result<CustomerSourceDTO> customerSource(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusMonths(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        CustomerSourceDTO dto = statisticsAdvancedService.getCustomerSourceAnalysis(startTime, endTime);
        return Result.success(dto);
    }

    /**
     * 获取业绩趋势
     */
    @GetMapping("/performance-trend")
    @RequirePermission("statistics:trend")
    @OperationLog(module = "数据统计", type = "查询", description = "查询业绩趋势")
    public Result<List<Map<String, Object>>> performanceTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "day") String type) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        List<Map<String, Object>> trend = statisticsAdvancedService.getPerformanceTrend(startTime, endTime, type);
        return Result.success(trend);
    }

    /**
     * 获取客户分布
     */
    @GetMapping("/customer-distribution")
    @RequirePermission("statistics:distribution")
    @OperationLog(module = "数据统计", type = "查询", description = "查询客户分布")
    public Result<List<Map<String, Object>>> customerDistribution(
            @RequestParam(defaultValue = "industry") String dimension) {
        
        List<Map<String, Object>> distribution = statisticsAdvancedService.getCustomerDistribution(dimension);
        return Result.success(distribution);
    }

    /**
     * 获取跟进方式统计
     */
    @GetMapping("/followup-stats")
    @RequirePermission("statistics:followup")
    @OperationLog(module = "数据统计", type = "查询", description = "查询跟进方式统计")
    public Result<Map<String, Object>> followupStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        if (startTime == null) {
            startTime = LocalDateTime.now().minusMonths(1);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }
        
        Map<String, Object> stats = statisticsAdvancedService.getFollowUpTypeStats(startTime, endTime);
        return Result.success(stats);
    }

    /**
     * 获取合同状态统计
     */
    @GetMapping("/contract-stats")
    @RequirePermission("statistics:contract")
    @OperationLog(module = "数据统计", type = "查询", description = "查询合同状态统计")
    public Result<Map<String, Object>> contractStats() {
        Map<String, Object> stats = statisticsAdvancedService.getContractStatusStats();
        return Result.success(stats);
    }

    /**
     * 获取商机阶段统计
     */
    @GetMapping("/opportunity-stats")
    @RequirePermission("statistics:opportunity")
    @OperationLog(module = "数据统计", type = "查询", description = "查询商机阶段统计")
    public Result<Map<String, Object>> opportunityStats() {
        Map<String, Object> stats = statisticsAdvancedService.getOpportunityStageStats();
        return Result.success(stats);
    }
}
