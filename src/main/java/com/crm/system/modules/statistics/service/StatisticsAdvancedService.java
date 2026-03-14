package com.crm.system.modules.statistics.service;

import com.crm.system.modules.statistics.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计分析服务接口（增强版）
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface StatisticsAdvancedService {

    /**
     * 获取销售排行榜
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量
     * @return 销售排行榜
     */
    List<SalesRankingDTO> getSalesRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取转化率分析
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 转化率分析
     */
    ConversionRateDTO getConversionRate(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取客户来源分析
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 客户来源分析
     */
    CustomerSourceDTO getCustomerSourceAnalysis(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取业绩趋势
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 类型（day/week/month）
     * @return 业绩趋势数据
     */
    List<Map<String, Object>> getPerformanceTrend(LocalDateTime startTime, LocalDateTime endTime, String type);

    /**
     * 获取客户分布（按行业/地区）
     * 
     * @param dimension 维度（industry/region）
     * @return 分布数据
     */
    List<Map<String, Object>> getCustomerDistribution(String dimension);

    /**
     * 获取跟进方式统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 跟进方式统计
     */
    Map<String, Object> getFollowUpTypeStats(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取合同状态统计
     * 
     * @return 合同状态统计
     */
    Map<String, Object> getContractStatusStats();

    /**
     * 获取商机阶段统计
     * 
     * @return 商机阶段统计
     */
    Map<String, Object> getOpportunityStageStats();
}
