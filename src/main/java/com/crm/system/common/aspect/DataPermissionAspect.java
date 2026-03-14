package com.crm.system.common.aspect;

import com.crm.system.common.annotation.DataPermission;
import com.crm.system.common.utils.DataPermissionCalculator;
import com.crm.system.common.utils.DataPermissionContext;
import com.crm.system.common.utils.UserContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据权限切面
 * 
 * 自动在查询方法中添加数据权限过滤
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Aspect
@Component
public class DataPermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(DataPermissionAspect.class);

    @Autowired
    private DataPermissionCalculator permissionCalculator;

    /**
     * 环绕通知
     */
    @Around("@annotation(com.crm.system.common.annotation.DataPermission)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取注解信息
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        DataPermission annotation = method.getAnnotation(DataPermission.class);

        // 获取当前用户 ID
        Long userId = UserContext.getUserId();

        // 计算数据权限 SQL
        String sqlFilter = null;
        if (userId != null && annotation.filter()) {
            sqlFilter = permissionCalculator.calculatePermissionFilter(
                    userId, 
                    annotation.value(), 
                    annotation.field()
            );
            
            // 设置到上下文中
            DataPermissionContext.setSqlFilter(sqlFilter);
            
            log.debug("用户 {} 的数据权限过滤：{}", userId, sqlFilter);
        }

        try {
            // 执行目标方法
            return point.proceed();
        } finally {
            // 清除上下文
            DataPermissionContext.clear();
        }
    }
}
