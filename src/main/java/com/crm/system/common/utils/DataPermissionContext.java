package com.crm.system.common.utils;

/**
 * 数据权限上下文
 * 
 * 使用 ThreadLocal 存储数据权限 SQL 片段
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
public class DataPermissionContext {

    private static final ThreadLocal<String> SQL_FILTER = new ThreadLocal<>();

    /**
     * 设置数据权限 SQL
     */
    public static void setSqlFilter(String sql) {
        SQL_FILTER.set(sql);
    }

    /**
     * 获取数据权限 SQL
     */
    public static String getSqlFilter() {
        return SQL_FILTER.get();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        SQL_FILTER.remove();
    }
}
