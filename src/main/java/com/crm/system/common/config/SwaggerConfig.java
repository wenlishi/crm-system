package com.crm.system.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置
 * 
 * @author wenlishi
 * @since 2026-03-16
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CRM 客户管理系统 API")
                        .version("v1.8.0")
                        .description("""
                                企业级 CRM 客户管理系统 - Java 学习实战项目
                                
                                **技术栈**：
                                - Spring Boot 3.2
                                - MyBatis-Plus 3.5.5
                                - JWT 认证
                                - Redis 缓存
                                - RBAC 权限管理
                                
                                **核心功能**：
                                - 客户管理、跟进记录、商机管理、合同管理
                                - 用户管理、角色管理、权限管理、数据字典
                                - 数据统计、操作日志、文件管理
                                
                                **接口总数**：70 个
                                """)
                        .contact(new Contact()
                                .name("wenlishi")
                                .url("https://github.com/wenlishi/crm-system"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
