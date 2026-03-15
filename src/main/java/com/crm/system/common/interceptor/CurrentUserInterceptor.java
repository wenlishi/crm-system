package com.crm.system.common.interceptor;

import com.crm.system.common.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 当前用户拦截器 - 从 Token 中提取用户信息并存入 ThreadLocal
 * 
 * @author wenlishi
 * @since 2026-03-16
 */
@Component
public class CurrentUserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取 Token
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            try {
                // 解析 Token 获取用户 ID
                Long userId = JwtUtil.getUserIdFromToken(token);
                String username = JwtUtil.getUsernameFromToken(token);
                
                // 存入 ThreadLocal
                if (userId != null) {
                    CurrentUserContext.setUserId(userId);
                    CurrentUserContext.setUsername(username);
                }
            } catch (Exception e) {
                // Token 无效或过期，忽略（由 JWT 拦截器处理）
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理 ThreadLocal，防止内存泄漏
        CurrentUserContext.clear();
    }
}
