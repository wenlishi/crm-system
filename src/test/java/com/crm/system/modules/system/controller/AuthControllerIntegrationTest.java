package com.crm.system.modules.system.controller;

import com.crm.system.common.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证 Controller 集成测试
 * 
 * @author wenlishi
 * @since 2026-03-17
 */
@DisplayName("认证管理集成测试")
class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("用户登录 - 成功")
    void testLogin_Success() throws Exception {
        String loginRequest = "{\"username\":\"admin\",\"password\":\"admin123\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("用户登录 - 失败（用户名不存在）")
    void testLogin_UserNotFound() throws Exception {
        String loginRequest = "{\"username\":\"notexist\",\"password\":\"123456\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("用户登录 - 失败（密码错误）")
    void testLogin_WrongPassword() throws Exception {
        String loginRequest = "{\"username\":\"admin\",\"password\":\"wrongpassword\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("用户登录 - 失败（用户名为空）")
    void testLogin_EmptyUsername() throws Exception {
        String loginRequest = "{\"username\":\"\",\"password\":\"admin123\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("用户登录 - 失败（密码为空）")
    void testLogin_EmptyPassword() throws Exception {
        String loginRequest = "{\"username\":\"admin\",\"password\":\"\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isBadRequest());
    }
}
