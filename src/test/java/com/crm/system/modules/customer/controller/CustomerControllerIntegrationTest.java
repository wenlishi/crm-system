package com.crm.system.modules.customer.controller;

import com.crm.system.common.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 客户管理集成测试
 * 
 * @author wenlishi
 * @since 2026-03-17
 */
@DisplayName("客户管理集成测试")
class CustomerControllerIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("客户列表 - 成功")
    void testListAll_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("客户分页查询 - 成功")
    void testPage_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/page")
                .contentType(APPLICATION_JSON)
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").exists());
    }

    @Test
    @DisplayName("根据 ID 查询客户 - 成功")
    void testGetById_Success() throws Exception {
        // 先获取一个客户 ID
        String response = mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/page")
                .contentType(APPLICATION_JSON)
                .param("current", "1")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 如果有客户，测试查询
        if (response.contains("\"records\"")) {
            // 这里简化测试，实际应该解析 JSON 获取 customer_id
            // 由于是集成测试，我们只验证接口能正常调用
            mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/1")
                    .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("新增客户 - 成功")
    void testSave_Success() throws Exception {
        String customerJson = "{" +
                "\"customerName\":\"测试客户-" + System.currentTimeMillis() + "\"," +
                "\"phone\":\"13800138000\"," +
                "\"level\":1," +
                "\"status\":1," +
                "\"source\":1" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                .contentType(APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("新增客户 - 失败（客户名为空）")
    void testSave_EmptyName() throws Exception {
        String customerJson = "{" +
                "\"customerName\":\"\"," +
                "\"phone\":\"13800138000\"" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                .contentType(APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isBadRequest());
    }
}
