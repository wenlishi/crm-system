package com.crm.system.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 
 * 用于标记需要数据权限控制的方法
 * 自动过滤查询结果（如：销售只能看自己的客户）
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /**
     * 数据权限类型
     * 
     * @return 权限类型
     */
    DataType value() default DataType.CUSTOMER;

    /**
     * 数据权限字段名
     * 默认为 owner_id（负责人 ID）
     * 
     * @return 字段名
     */
    String field() default "owner_id";

    /**
     * 是否过滤（超级管理员不过滤）
     * 
     * @return true=过滤，false=不过滤
     */
    boolean filter() default true;
}
