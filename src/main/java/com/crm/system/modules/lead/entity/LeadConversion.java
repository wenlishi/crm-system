package com.crm.system.modules.lead.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 线索转化记录实体类
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@Data
@TableName("crm_lead_conversion")
public class LeadConversion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 转化 ID
     */
    @TableId(value = "conversion_id", type = IdType.ASSIGN_ID)
    private Long conversionId;

    /**
     * 线索 ID
     */
    private Long leadId;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 转化人 ID
     */
    private Long convertedBy;

    /**
     * 转化时间
     */
    private LocalDateTime convertedTime;

    /**
     * 转化前线索状态
     */
    private Integer leadStatusBefore;

    /**
     * 转化前线索级别
     */
    private Integer leadLevelBefore;

    /**
     * 转化后客户级别
     */
    private Integer customerLevelAfter;

    /**
     * 转化备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
