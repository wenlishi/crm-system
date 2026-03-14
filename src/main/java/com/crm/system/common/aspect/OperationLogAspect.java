package com.crm.system.common.aspect;

import com.crm.system.common.annotation.OperationLog;
import com.crm.system.common.utils.UserContext;
import com.crm.system.modules.system.entity.OperationLog;
import com.crm.system.modules.system.service.OperationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作日志切面
 * 
 * 自动记录带有 @OperationLog 注解的方法执行信息
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 环绕通知
     */
    @Around("@annotation(com.crm.system.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
        
        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        
        // 构建操作日志对象
        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(UserContext.getUserId());
        operationLog.setUsername(UserContext.getUsername());
        operationLog.setModule(annotation.module());
        operationLog.setType(annotation.type());
        operationLog.setDescription(annotation.description());
        operationLog.setMethod(request.getMethod());
        operationLog.setUrl(request.getRequestURI());
        operationLog.setIp(getIpAddress(request));
        operationLog.setBrowser(getBrowser(request));
        operationLog.setOs(getOs(request));
        
        // 记录请求参数
        try {
            String params = objectMapper.writeValueAsString(point.getArgs());
            // 限制参数长度
            if (params.length() > 2000) {
                params = params.substring(0, 2000) + "...";
            }
            operationLog.setParams(params);
        } catch (Exception e) {
            log.warn("记录请求参数失败", e);
            operationLog.setParams("参数解析失败");
        }
        
        Object result = null;
        try {
            // 执行目标方法
            result = point.proceed();
            
            // 记录成功
            operationLog.setStatus(1);
            operationLog.setResult("成功");
        } catch (Throwable e) {
            // 记录失败
            operationLog.setStatus(0);
            operationLog.setErrorMsg(e.getMessage());
            operationLog.setResult("失败：" + e.getMessage());
            log.error("操作执行失败：{}", annotation.description(), e);
            throw e;
        } finally {
            // 计算执行时长
            long duration = System.currentTimeMillis() - startTime;
            operationLog.setDuration(duration);
            operationLog.setCreateTime(LocalDateTime.now());
            
            // 异步保存日志（不阻塞主流程）
            try {
                operationLogService.logOperation(operationLog);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
        
        return result;
    }

    /**
     * 获取 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    /**
     * 获取浏览器信息
     */
    private String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            return "Internet Explorer";
        } else if (userAgent.contains("Edg")) {
            return "Edge";
        }
        
        return "Other";
    }

    /**
     * 获取操作系统信息
     */
    private String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac")) {
            return "Mac";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        }
        
        return "Other";
    }
}
