package com.crm.system.modules.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.system.modules.customer.entity.Customer;
import com.crm.system.modules.customer.mapper.CustomerMapper;
import com.crm.system.modules.follow.entity.FollowUp;
import com.crm.system.modules.follow.mapper.FollowUpMapper;
import com.crm.system.modules.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计服务实现类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private FollowUpMapper followUpMapper;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // 客户总数
        Long totalCustomers = customerMapper.selectCount(null);
        stats.put("totalCustomers", totalCustomers);

        // 有效客户数
        LambdaQueryWrapper<Customer> validWrapper = new LambdaQueryWrapper<>();
        validWrapper.eq(Customer::getStatus, 1);
        Long validCustomers = customerMapper.selectCount(validWrapper);
        stats.put("validCustomers", validCustomers);

        // 今日新增客户
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<Customer> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.ge(Customer::getCreateTime, todayStart);
        Long todayCustomers = customerMapper.selectCount(todayWrapper);
        stats.put("todayCustomers", todayCustomers);

        // 跟进记录总数
        Long totalFollowUps = followUpMapper.selectCount(null);
        stats.put("totalFollowUps", totalFollowUps);

        // 本周跟进次数
        LocalDate weekStart = LocalDate.now().minusDays(7);
        LambdaQueryWrapper<FollowUp> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.ge(FollowUp::getCreateTime, weekStart.atStartOfDay());
        Long weekFollowUps = followUpMapper.selectCount(weekWrapper);
        stats.put("weekFollowUps", weekFollowUps);

        return stats;
    }

    @Override
    public Map<String, Object> getCustomerGrowthStats() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> dailyStats = new ArrayList<>();

        // 近 7 天每天的客户数
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Customer::getCreateTime, start)
                   .lt(Customer::getCreateTime, end);

            Long count = customerMapper.selectCount(wrapper);

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayStat.put("count", count);
            dailyStats.add(dayStat);
        }

        result.put("dailyStats", dailyStats);
        return result;
    }

    @Override
    public Map<String, Object> getFollowUpStats() {
        Map<String, Object> result = new HashMap<>();

        // 按跟进方式统计
        Map<String, Long> byType = new HashMap<>();
        byType.put("电话", countFollowUpsByType(1));
        byType.put("微信", countFollowUpsByType(2));
        byType.put("邮件", countFollowUpsByType(3));
        byType.put("面谈", countFollowUpsByType(4));
        byType.put("其他", countFollowUpsByType(5));

        result.put("byType", byType);

        // 近 7 天跟进趋势
        List<Map<String, Object>> trendStats = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = start.plusDays(1);

            LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(FollowUp::getCreateTime, start)
                   .lt(FollowUp::getCreateTime, end);

            Long count = followUpMapper.selectCount(wrapper);

            Map<String, Object> dayStat = new HashMap<>();
            dayStat.put("date", date.format(DateTimeFormatter.ofPattern("MM-dd")));
            dayStat.put("count", count);
            trendStats.add(dayStat);
        }

        result.put("trendStats", trendStats);
        return result;
    }

    private Long countFollowUpsByType(Integer type) {
        LambdaQueryWrapper<FollowUp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUp::getFollowType, type);
        return followUpMapper.selectCount(wrapper);
    }
}
