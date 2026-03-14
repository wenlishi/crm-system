package com.crm.system.common.interceptor;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 数据权限拦截器（MyBatis-Plus 插件）
 * 
 * 自动在 SQL 中添加数据权限过滤条件
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Component
public class DataPermissionInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, 
                           RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        // 获取数据权限 SQL 过滤条件
        String sqlFilter = DataPermissionContext.getSqlFilter();
        
        if (sqlFilter != null && !sqlFilter.isEmpty()) {
            // 在 SQL 中添加 WHERE 条件
            String originalSql = boundSql.getSql();
            
            // 判断是否已有 WHERE 条件
            if (originalSql.toUpperCase().contains(" WHERE ")) {
                // 已有 WHERE，使用 AND 连接
                String newSql = originalSql.replaceFirst(
                    "(?i)\\s+WHERE\\s+", 
                    " WHERE " + sqlFilter + " AND "
                );
                // 使用反射修改 SQL
                try {
                    java.lang.reflect.Field field = BoundSql.class.getDeclaredField("sql");
                    field.setAccessible(true);
                    field.set(boundSql, newSql);
                } catch (Exception e) {
                    // 忽略错误
                }
            }
        }
    }

    @Override
    public void setProperties(Properties properties) {
        // 无需配置
    }
}
