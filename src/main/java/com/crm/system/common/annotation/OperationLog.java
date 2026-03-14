package com.crm.system.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 
 * 用于记录用户操作日志
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     * 例如：客户管理、商机管理、合同管理
     * 
     * @return 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     * 例如：新增、修改、删除、查询
     * 
     * @return 操作类型
     */
    String type() default "";

    /**
     * 操作描述
     * 例如：新增客户、修改合同
     * 
     * @return 操作描述
     */
    String description() default "";
}
