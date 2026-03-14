package com.crm.system.common.interceptor;

import com.crm.system.common.annotation.RequirePermission;
import com.crm.system.common.utils.JwtUtil;
import com.crm.system.common.utils.PermissionChecker;
import com.crm.system.common.utils.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 拦截器（支持权限校验）
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private PermissionChecker permissionChecker;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求直接放行（CORS 预检）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 放行登录、注册接口
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/login") || 
            requestURI.startsWith("/api/auth/register")) {
            return true;
        }

        // 获取 Token
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            sendErrorResponse(response, 401, "未授权，请先登录");
            return false;
        }

        // 验证 Token
        if (!JwtUtil.validateToken(token)) {
            sendErrorResponse(response, 401, "Token 无效或已过期");
            return false;
        }

        // 获取用户 ID 并存入上下文
        Long userId = JwtUtil.getUserIdFromToken(token);
        UserContext.setUserId(userId);
        
        // 获取用户名（可选）
        String username = JwtUtil.getUsernameFromToken(token);
        if (username != null) {
            UserContext.setUsername(username);
        }

        // 权限校验（如果方法上有 @RequirePermission 注解）
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
            
            if (requirePermission != null) {
                // 检查单个权限
                String permissionCode = requirePermission.value();
                if (!permissionCode.isEmpty()) {
                    if (!permissionChecker.hasPermission(userId, permissionCode)) {
                        sendErrorResponse(response, 403, "没有权限执行此操作：" + permissionCode);
                        return false;
                    }
                }
                
                // 检查多个权限（满足任一）
                String[] anyOf = requirePermission.anyOf();
                if (anyOf != null && anyOf.length > 0) {
                    if (requirePermission.all()) {
                        // 需要所有权限
                        if (!permissionChecker.hasAllPermissions(userId, anyOf)) {
                            sendErrorResponse(response, 403, "没有权限执行此操作");
                            return false;
                        }
                    } else {
                        // 满足任一即可
                        if (!permissionChecker.hasAnyPermission(userId, anyOf)) {
                            sendErrorResponse(response, 403, "没有权限执行此操作");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", status);
        result.put("message", message);
        result.put("data", null);
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) throws Exception {
        // 清除用户上下文，防止内存泄漏
        UserContext.clear();
    }
}
