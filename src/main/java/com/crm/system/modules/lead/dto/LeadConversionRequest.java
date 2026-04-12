package com.crm.system.modules.lead.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 线索转化请求 DTO
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@Data
public class LeadConversionRequest {

    /**
     * 线索 ID
     */
    private Long leadId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型（1 个人 2 企业）
     */
    private Integer customerType = 1;

    /**
     * 客户级别（1 普通 2 VIP 3 重要）
     */
    private Integer customerLevel = 1;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 预算
     */
    private BigDecimal budget;

    /**
     * 转化备注
     */
    private String remark;

    /**
     * 转化人 ID
     */
    private Long convertedBy;
}
