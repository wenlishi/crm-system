package com.crm.system.modules.contract.controller;

import com.crm.system.common.IntegrationTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 合同管理 Controller 集成测试
 * 
 * @author wenlishi
 * @since 2026-03-18
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("合同管理模块集成测试")
class ContractControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试创建合同 - 成功
     */
    @Test
    @DisplayName("创建合同 - 成功")
    void testSaveContract_Success() throws Exception {
        // Arrange
        Map<String, Object> contract = new HashMap<>();
        contract.put("customerId", 1);
        contract.put("opportunityId", 1);
        contract.put("contractNo", "HT-2026-001");
        contract.put("amount", new BigDecimal("100000.00"));
        contract.put("startDate", "2026-03-18");
        contract.put("endDate", "2027-03-17");
        contract.put("status", "草稿");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/contracts")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contract)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.contractNo").value("HT-2026-001"));
    }

    /**
     * 测试创建合同 - 金额为空
     */
    @Test
    @DisplayName("创建合同 - 金额为空")
    void testSaveContract_NullAmount() throws Exception {
        // Arrange
        Map<String, Object> contract = new HashMap<>();
        contract.put("customerId", 1);
        contract.put("contractNo", "HT-2026-002");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/contracts")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contract)))
            .andExpect(status().isBadRequest());
    }

    /**
     * 测试更新合同状态 - 成功
     */
    @Test
    @DisplayName("更新合同状态 - 成功")
    void testUpdateContractStatus_Success() throws Exception {
        // Arrange
        Map<String, Object> update = new HashMap<>();
        update.put("status", "审核中");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/contracts/1/status")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试查询客户合同列表 - 成功
     */
    @Test
    @DisplayName("查询客户合同列表 - 成功")
    void testListByCustomer_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试查询客户合同列表 - 无记录
     */
    @Test
    @DisplayName("查询客户合同列表 - 无记录")
    void testListByCustomer_NoRecords() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "999999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    /**
     * 测试按状态分页查询 - 成功
     */
    @Test
    @DisplayName("按状态分页查询 - 成功")
    void testPageByStatus_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts/page")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("status", "执行中")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.total").exists());
    }

    /**
     * 测试获取合同统计
     */
    @Test
    @DisplayName("获取合同统计 - 成功")
    void testGetContractStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts/stats")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.totalAmount").exists())
            .andExpect(jsonPath("$.data.byStatus").exists());
    }

    /**
     * 测试查询合同详情 - 成功
     */
    @Test
    @DisplayName("查询合同详情 - 成功")
    void testGetContractById_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts/1")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试查询合同详情 - 不存在
     */
    @Test
    @DisplayName("查询合同详情 - 不存在")
    void testGetContractById_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts/999999")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 测试删除合同 - 成功
     */
    @Test
    @DisplayName("删除合同 - 成功")
    void testDeleteContract_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/contracts/1")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试未授权访问
     */
    @Test
    @DisplayName("未授权访问 - 失败")
    void testListContracts_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/contracts")
                .param("customerId", "1"))
            .andExpect(status().isUnauthorized());
    }
}
