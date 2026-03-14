package com.crm.system.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@ToString
@TableName("sys_operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志 ID
     */
    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private Long logId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求 URL
     */
    private String url;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 响应结果
     */
    private String result;

    /**
     * 执行时长（毫秒）
     */
    private Long duration;

    /**
     * IP 地址
     */
    private String ip;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 操作状态（0 失败 1 成功）
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
