package com.crm.system.modules.statistics.service;

import com.crm.system.modules.statistics.dto.SalesRankingDTO;
import com.crm.system.modules.statistics.dto.ConversionRateDTO;
import com.crm.system.modules.statistics.service.impl.StatisticsAdvancedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统计服务单元测试
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
class StatisticsAdvancedServiceTest {

    private StatisticsAdvancedServiceImpl statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsAdvancedServiceImpl();
    }

    @Test
    void testGetSalesRanking() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endTime = LocalDateTime.now();
        Integer limit = 10;

        // Act
        List<SalesRankingDTO> result = statisticsService.getSalesRanking(startTime, endTime, limit);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals(1, result.get(0).getRank());
        assertNotNull(result.get(0).getUsername());
    }

    @Test
    void testGetConversionRate() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endTime = LocalDateTime.now();

        // Act
        ConversionRateDTO result = statisticsService.getConversionRate(startTime, endTime);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTotalCustomers());
        assertNotNull(result.getCustomerToOpportunityRate());
        assertNotNull(result.getFunnelStages());
        assertFalse(result.getFunnelStages().isEmpty());
    }

    @Test
    void testGetCustomerSourceAnalysis() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endTime = LocalDateTime.now();

        // Act
        CustomerSourceDTO result = statisticsService.getCustomerSourceAnalysis(startTime, endTime);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getSources());
        assertFalse(result.getSources().isEmpty());
    }

    @Test
    void testGetPerformanceTrend() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        String type = "day";

        // Act
        List<Map<String, Object>> result = statisticsService.getPerformanceTrend(startTime, endTime, type);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(30, result.size()); // 30 天
    }

    @Test
    void testGetCustomerDistribution_Industry() {
        // Arrange
        String dimension = "industry";

        // Act
        List<Map<String, Object>> result = statisticsService.getCustomerDistribution(dimension);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(item -> "互联网".equals(item.get("name"))));
    }

    @Test
    void testGetCustomerDistribution_Region() {
        // Arrange
        String dimension = "region";

        // Act
        List<Map<String, Object>> result = statisticsService.getCustomerDistribution(dimension);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(item -> "华北".equals(item.get("name"))));
    }

    @Test
    void testGetFollowUpTypeStats() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime endTime = LocalDateTime.now();

        // Act
        Map<String, Object> result = statisticsService.getFollowUpTypeStats(startTime, endTime);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("byType"));
        assertTrue(result.containsKey("total"));
    }

    @Test
    void testGetContractStatusStats() {
        // Act
        Map<String, Object> result = statisticsService.getContractStatusStats();

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("byStatus"));
        assertTrue(result.containsKey("total"));
    }

    @Test
    void testGetOpportunityStageStats() {
        // Act
        Map<String, Object> result = statisticsService.getOpportunityStageStats();

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("byStage"));
        assertTrue(result.containsKey("total"));
    }

    @Test
    void testCalculateRate() {
        // Arrange
        Integer total = 100;
        Integer success = 50;

        // Act
        Double rate = calculateRate(total, success);

        // Assert
        assertNotNull(rate);
        assertEquals(50.0, rate);
    }

    @Test
    void testCalculateRate_ZeroTotal() {
        // Arrange
        Integer total = 0;
        Integer success = 50;

        // Act
        Double rate = calculateRate(total, success);

        // Assert
        assertNotNull(rate);
        assertEquals(0.0, rate);
    }

    // 辅助方法
    private Double calculateRate(Integer total, Integer success) {
        if (total == null || total == 0) {
            return 0.0;
        }
        return (double) success / total * 100;
    }
}
