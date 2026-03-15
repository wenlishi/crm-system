package com.crm.system.common.interceptor;

/**
 * 当前用户上下文（ThreadLocal）
 * 
 * @author wenlishi
 * @since 2026-03-16
 */
public class CurrentUserContext {

    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     */
    public static void setUserId(Long userId) {
        userIdHolder.set(userId);
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getUserId() {
        return userIdHolder.get();
    }

    /**
     * 设置当前用户名
     */
    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return usernameHolder.get();
    }

    /**
     * 清理当前用户信息
     */
    public static void clear() {
        userIdHolder.remove();
        usernameHolder.remove();
    }
}
