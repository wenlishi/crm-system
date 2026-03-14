package com.crm.system.modules.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.contract.entity.Contract;
import com.crm.system.modules.follow.entity.FollowUp;
import com.crm.system.modules.opportunity.entity.Opportunity;
import com.crm.system.modules.statistics.dto.*;
import com.crm.system.modules.statistics.service.StatisticsAdvancedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计分析服务实现类（增强版）
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Slf4j
@Service
public class StatisticsAdvancedServiceImpl implements StatisticsAdvancedService {

    @Override
    public List<SalesRankingDTO> getSalesRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        // TODO: 需要关联用户表，这里简化实现
        List<SalesRankingDTO> ranking = new ArrayList<>();
        
        // 示例数据
        for (int i = 1; i <= (limit != null ? limit : 10); i++) {
            SalesRankingDTO dto = new SalesRankingDTO();
            dto.setUserId((long) i);
            dto.setUsername("销售" + i);
            dto.setDept("销售部");
            dto.setTotalCustomers(100 - i * 5);
            dto.setDealCustomers(50 - i * 2);
            dto.setDealRate(BigDecimal.valueOf(50.0 - i).setScale(2, RoundingMode.HALF_UP));
            dto.setTotalContracts(30 - i);
            dto.setTotalAmount(BigDecimal.valueOf(100000 * (10 - i)));
            dto.setRank(i);
            ranking.add(dto);
        }
        
        return ranking;
    }

    @Override
    public ConversionRateDTO getConversionRate(LocalDateTime startTime, LocalDateTime endTime) {
        ConversionRateDTO dto = new ConversionRateDTO();
        
        // 模拟数据
        dto.setTotalCustomers(500);
        dto.setValidCustomers(450);
        dto.setTotalOpportunities(300);
        dto.setDealOpportunities(150);
        dto.setTotalContracts(200);
        dto.setDealContracts(180);
        
        // 计算转化率
        dto.setCustomerToOpportunityRate(calculateRate(500, 300));
        dto.setOpportunityToContractRate(calculateRate(300, 200));
        dto.setOverallConversionRate(calculateRate(500, 180));
        
        // 销售漏斗各阶段
        List<ConversionRateDTO.FunnelStageDTO> stages = new ArrayList<>();
        stages.add(createStage("初步接触", 300, 100.0));
        stages.add(createStage("需求确认", 250, 83.3));
        stages.add(createStage("方案报价", 200, 66.7));
        stages.add(createStage("谈判", 180, 60.0));
        stages.add(createStage("成交", 150, 50.0));
        
        dto.setFunnelStages(stages);
        
        return dto;
    }

    @Override
    public CustomerSourceDTO getCustomerSourceAnalysis(LocalDateTime startTime, LocalDateTime endTime) {
        CustomerSourceDTO dto = new CustomerSourceDTO();
        
        List<CustomerSourceDTO> sources = new ArrayList<>();
        
        // 模拟数据
        sources.add(createSource("线上广告", 200, 40.0, 80, 40.0));
        sources.add(createSource("客户推荐", 150, 30.0, 90, 60.0));
        sources.add(createSource("电话销售", 100, 20.0, 30, 30.0));
        sources.add(createSource("展会活动", 50, 10.0, 25, 50.0));
        
        dto.setSources(sources);
        
        return dto;
    }

    @Override
    public List<Map<String, Object>> getPerformanceTrend(LocalDateTime startTime, LocalDateTime endTime, String type) {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = startTime.toLocalDate();
        LocalDate end = endTime.toLocalDate();
        
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", date.format(formatter));
            data.put("newCustomers", new Random().nextInt(50));
            data.put("newOpportunities", new Random().nextInt(30));
            data.put("newContracts", new Random().nextInt(20));
            data.put("amount", new BigDecimal(new Random().nextInt(100000)));
            trend.add(data);
        }
        
        return trend;
    }

    @Override
    public List<Map<String, Object>> getCustomerDistribution(String dimension) {
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        if ("industry".equals(dimension)) {
            distribution.add(createDistribution("互联网", 150, 30.0));
            distribution.add(createDistribution("金融", 120, 24.0));
            distribution.add(createDistribution("教育", 100, 20.0));
            distribution.add(createDistribution("医疗", 80, 16.0));
            distribution.add(createDistribution("其他", 50, 10.0));
        } else if ("region".equals(dimension)) {
            distribution.add(createDistribution("华北", 200, 40.0));
            distribution.add(createDistribution("华东", 150, 30.0));
            distribution.add(createDistribution("华南", 100, 20.0));
            distribution.add(createDistribution("其他", 50, 10.0));
        }
        
        return distribution;
    }

    @Override
    public Map<String, Object> getFollowUpTypeStats(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Integer> byType = new HashMap<>();
        byType.put("电话", 500);
        byType.put("微信", 300);
        byType.put("邮件", 150);
        byType.put("面谈", 100);
        byType.put("其他", 50);
        
        stats.put("byType", byType);
        stats.put("total", 1100);
        
        return stats;
    }

    @Override
    public Map<String, Object> getContractStatusStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Integer> byStatus = new HashMap<>();
        byStatus.put("草稿", 20);
        byStatus.put("待审核", 30);
        byStatus.put("已审核", 50);
        byStatus.put("执行中", 100);
        byStatus.put("已完成", 200);
        byStatus.put("已终止", 10);
        
        stats.put("byStatus", byStatus);
        stats.put("total", 410);
        
        return stats;
    }

    @Override
    public Map<String, Object> getOpportunityStageStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Integer> byStage = new HashMap<>();
        byStage.put("初步接触", 300);
        byStage.put("需求确认", 250);
        byStage.put("方案报价", 200);
        byStage.put("谈判", 180);
        byStage.put("成交", 150);
        
        stats.put("byStage", byStage);
        stats.put("total", 1080);
        
        return stats;
    }

    /**
     * 计算转化率
     */
    private Double calculateRate(Integer total, Integer success) {
        if (total == null || total == 0) {
            return 0.0;
        }
        return BigDecimal.valueOf(success)
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * 创建漏斗阶段
     */
    private ConversionRateDTO.FunnelStageDTO createStage(String name, Integer count, Double rate) {
        ConversionRateDTO.FunnelStageDTO stage = new ConversionRateDTO.FunnelStageDTO();
        stage.setStageName(name);
        stage.setCount(count);
        stage.setConversionRate(rate);
        return stage;
    }

    /**
     * 创建来源数据
     */
    private CustomerSourceDTO createSource(String source, Integer count, Double percentage, 
                                           Integer dealCount, Double dealRate) {
        CustomerSourceDTO dto = new CustomerSourceDTO();
        dto.setSource(source);
        dto.setCount(count);
        dto.setPercentage(percentage);
        dto.setDealCount(dealCount);
        dto.setDealRate(dealRate);
        return dto;
    }

    /**
     * 创建分布数据
     */
    private Map<String, Object> createDistribution(String name, Integer count, Double percentage) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", count);
        data.put("percentage", percentage);
        return data;
    }
}
