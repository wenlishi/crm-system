package com.crm.system.modules.statistics.controller;

import com.crm.system.common.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 统计 Controller 集成测试
 * 
 * @author wenlishi
 * @since 2026-03-18
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("统计模块集成测试")
class StatisticsControllerIntegrationTest extends IntegrationTestBase {

    /**
     * 测试获取仪表盘统计数据
     */
    @Test
    @DisplayName("获取仪表盘统计 - 成功")
    void testGetDashboardStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/statistics/dashboard")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.totalCustomers").exists())
            .andExpect(jsonPath("$.data.totalOpportunities").exists())
            .andExpect(jsonPath("$.data.totalContracts").exists());
    }

    /**
     * 测试获取客户增长统计
     */
    @Test
    @DisplayName("获取客户增长统计 - 成功")
    void testGetCustomerGrowthStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/statistics/customer-growth")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.growthTrend").exists());
    }

    /**
     * 测试获取跟进记录统计
     */
    @Test
    @DisplayName("获取跟进记录统计 - 成功")
    void testGetFollowUpStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/statistics/follow-up")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.byType").exists())
            .andExpect(jsonPath("$.data.total").exists());
    }

    /**
     * 测试获取客户统计（按等级）
     */
    @Test
    @DisplayName("获取客户统计 - 成功")
    void testGetCustomerStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/statistics/customers")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.byLevel").exists())
            .andExpect(jsonPath("$.data.byStatus").exists());
    }

    /**
     * 测试获取合同统计
     */
    @Test
    @DisplayName("获取合同统计 - 成功")
    void testGetContractStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/statistics/contracts")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.totalAmount").exists())
            .andExpect(jsonPath("$.data.byStatus").exists());
    }

    /**
     * 测试未授权访问
     */
    @Test
    @DisplayName("未授权访问 - 失败")
    void testGetStats_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/statistics/dashboard"))
            .andExpect(status().isUnauthorized());
    }
}
