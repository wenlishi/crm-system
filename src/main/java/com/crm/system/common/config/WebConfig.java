package com.crm.system.common.config;

import com.crm.system.common.interceptor.CurrentUserInterceptor;
import com.crm.system.common.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private CurrentUserInterceptor currentUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 拦截器（验证 Token 有效性）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/**",
                    "/error"
                );
        
        // 当前用户拦截器（从 Token 中提取用户信息）
        registry.addInterceptor(currentUserInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/error"
                );
    }
}
