package com.crm.system.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 
 * 用于标记需要权限控制的接口方法
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限编码
     * 例如：customer:add, customer:edit, system:user:delete
     * 
     * @return 权限编码
     */
    String value() default "";

    /**
     * 权限编码数组（支持多个权限，满足其一即可）
     * 例如：{"customer:add", "customer:edit"}
     * 
     * @return 权限编码数组
     */
    String[] anyOf() default {};

    /**
     * 是否需要所有权限（与 anyOf 互斥）
     * 
     * @return true=需要所有权限，false=满足其一即可
     */
    boolean all() default false;
}
