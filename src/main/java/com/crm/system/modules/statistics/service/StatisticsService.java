package com.crm.system.modules.statistics.service;

import java.util.Map;

/**
 * 统计服务接口
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public interface StatisticsService {

    /**
     * 获取首页统计数据
     */
    Map<String, Object> getDashboardStats();

    /**
     * 获取客户增长统计（近 7 天）
     */
    Map<String, Object> getCustomerGrowthStats();

    /**
     * 获取跟进记录统计
     */
    Map<String, Object> getFollowUpStats();
}
