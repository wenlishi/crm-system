package com.crm.system.filter;

import com.crm.system.common.utils.JwtUtil;
import com.crm.system.common.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证过滤器
 * 
 * 功能：
 * 1. 拦截请求，解析 JWT Token
 * 2. 验证 Token 有效性
 * 3. 设置 SpringSecurity 认证上下文
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 1. 获取 Token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                // 2. 验证 Token 并获取用户信息
                Long userId = JwtUtil.getUserIdFromToken(token);
                String username = JwtUtil.getUsernameFromToken(token);
                
                // 3. 设置用户上下文（供业务代码使用）
                UserContext.setUserId(userId);
                UserContext.setUsername(username);
                
                // 4. 设置 SpringSecurity 认证上下文
                if (userId != null && username != null) {
                    // 从数据库或 Token 中获取角色（这里简化处理，使用默认角色）
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                        );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                
            } catch (Exception e) {
                logger.error("JWT Token 解析失败", e);
                // Token 无效，继续执行（接口会有权限校验）
            }
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从 Authorization 头获取
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 从参数获取（备选方案）
        return request.getParameter("token");
    }

    /**
     * 解析角色字符串为权限列表
     * 例如："admin,user" → [ROLE_admin, ROLE_user]
     */
    private List<SimpleGrantedAuthority> parseAuthorities(String roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        if (StringUtils.hasText(roles)) {
            String[] roleArray = roles.split(",");
            for (String role : roleArray) {
                String roleName = role.trim();
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                authorities.add(new SimpleGrantedAuthority(roleName));
            }
        } else {
            // 默认角色：USER
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
}
