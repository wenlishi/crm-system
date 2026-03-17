package com.crm.system.modules.follow.controller;

import com.crm.system.common.IntegrationTestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 跟进记录 Controller 集成测试
 * 
 * @author wenlishi
 * @since 2026-03-18
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("跟进记录模块集成测试")
class FollowUpControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试添加跟进记录 - 成功
     */
    @Test
    @DisplayName("添加跟进记录 - 成功")
    void testAddFollowUp_Success() throws Exception {
        // Arrange
        Map<String, Object> followUp = new HashMap<>();
        followUp.put("customerId", 1);
        followUp.put("content", "电话沟通，客户对产品很感兴趣");
        followUp.put("type", "电话");
        followUp.put("nextFollowUpTime", "2026-03-25 10:00:00");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/follow-ups")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followUp)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.content").value("电话沟通，客户对产品很感兴趣"));
    }

    /**
     * 测试添加跟进记录 - 客户不存在
     */
    @Test
    @DisplayName("添加跟进记录 - 客户不存在")
    void testAddFollowUp_CustomerNotFound() throws Exception {
        // Arrange
        Map<String, Object> followUp = new HashMap<>();
        followUp.put("customerId", 999999);
        followUp.put("content", "测试跟进");
        followUp.put("type", "电话");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/follow-ups")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followUp)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(404));
    }

    /**
     * 测试添加跟进记录 - 内容为空
     */
    @Test
    @DisplayName("添加跟进记录 - 内容为空")
    void testAddFollowUp_EmptyContent() throws Exception {
        // Arrange
        Map<String, Object> followUp = new HashMap<>();
        followUp.put("customerId", 1);
        followUp.put("content", "");
        followUp.put("type", "电话");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/follow-ups")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followUp)))
            .andExpect(status().isBadRequest());
    }

    /**
     * 测试查询客户跟进历史 - 有记录
     */
    @Test
    @DisplayName("查询跟进历史 - 有记录")
    void testListByCustomerId_WithRecords() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/follow-ups")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试查询客户跟进历史 - 无记录
     */
    @Test
    @DisplayName("查询跟进历史 - 无记录")
    void testListByCustomerId_NoRecords() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/follow-ups")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "999999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    /**
     * 测试分页查询跟进记录
     */
    @Test
    @DisplayName("分页查询跟进记录 - 成功")
    void testPageByCustomerId_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/follow-ups/page")
                .header("Authorization", "Bearer " + getAdminToken())
                .param("customerId", "1")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.records").isArray())
            .andExpect(jsonPath("$.data.total").exists())
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.size").value(10));
    }

    /**
     * 测试更新跟进记录 - 成功
     */
    @Test
    @DisplayName("更新跟进记录 - 成功")
    void testUpdateFollowUp_Success() throws Exception {
        // Arrange
        Map<String, Object> followUp = new HashMap<>();
        followUp.put("content", "更新后的跟进内容");
        followUp.put("type", "拜访");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/follow-ups/1")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followUp)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试删除跟进记录 - 成功
     */
    @Test
    @DisplayName("删除跟进记录 - 成功")
    void testDeleteFollowUp_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/follow-ups/1")
                .header("Authorization", "Bearer " + getAdminToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试未授权访问
     */
    @Test
    @DisplayName("未授权访问 - 失败")
    void testListFollowUps_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/follow-ups")
                .param("customerId", "1"))
            .andExpect(status().isUnauthorized());
    }
}
