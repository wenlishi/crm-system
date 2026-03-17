package com.crm.system.modules.opportunity.controller;

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
 * 商机管理 Controller 集成测试
 * 
 * @author wenlishi
 * @since 2026-03-18
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("商机管理模块集成测试")
class OpportunityControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试创建商机 - 成功
     */
    @Test
    @DisplayName("创建商机 - 成功")
    void testSaveOpportunity_Success() throws Exception {
        // Arrange
        Map<String, Object> opportunity = new HashMap<>();
        opportunity.put("customerId", 1);
        opportunity.put("title", "企业软件采购项目");
        opportunity.put("amount", new BigDecimal("50000.00"));
        opportunity.put("stage", "初步接洽");
        opportunity.put("probability", 30);
        opportunity.put("expectedCloseDate", "2026-06-30");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/opportunities")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opportunity)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.title").value("企业软件采购项目"));
    }

    /**
     * 测试创建商机 - 金额为空
     */
    @Test
    @DisplayName("创建商机 - 金额为空")
    void testSaveOpportunity_NullAmount() throws Exception {
        // Arrange
        Map<String, Object> opportunity = new HashMap<>();
        opportunity.put("customerId", 1);
        opportunity.put("title", "测试商机");
        opportunity.put("stage", "初步接洽");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/opportunities")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opportunity)))
            .andExpect(status().isBadRequest());
    }

    /**
     * 测试更新商机 - 成功
     */
    @Test
    @DisplayName("更新商机 - 成功")
    void testUpdateOpportunity_Success() throws Exception {
        // Arrange
        Map<String, Object> opportunity = new HashMap<>();
        opportunity.put("title", "更新后的项目名称");
        opportunity.put("stage", "需求分析");
        opportunity.put("probability", 50);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/opportunities/1")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(opportunity)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试查询客户商机列表 - 成功
     */
    @Test
    @DisplayName("查询客户商机列表 - 成功")
    void testListByCustomerId_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/opportunities")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试查询客户商机列表 - 无记录
     */
    @Test
    @DisplayName("查询客户商机列表 - 无记录")
    void testListByCustomerId_NoRecords() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/opportunities")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "999999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    /**
     * 测试按阶段分页查询 - 成功
     */
    @Test
    @DisplayName("按阶段分页查询 - 成功")
    void testPageByStage_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/opportunities/page")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("stage", "初步接洽")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.total").exists());
    }

    /**
     * 测试获取商机阶段统计
     */
    @Test
    @DisplayName("获取商机阶段统计 - 成功")
    void testGetStageStats_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/opportunities/stage-stats")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.byStage").exists());
    }

    /**
     * 测试删除商机 - 成功
     */
    @Test
    @DisplayName("删除商机 - 成功")
    void testDeleteOpportunity_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/opportunities/1")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试删除商机 - 不存在
     */
    @Test
    @DisplayName("删除商机 - 不存在")
    void testDeleteOpportunity_NotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/opportunities/999999")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 测试未授权访问
     */
    @Test
    @DisplayName("未授权访问 - 失败")
    void testListOpportunities_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/opportunities")
                .param("customerId", "1"))
            .andExpect(status().isUnauthorized());
    }
}
