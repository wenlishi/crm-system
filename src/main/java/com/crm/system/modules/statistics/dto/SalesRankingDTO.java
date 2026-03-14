package com.crm.system.modules.statistics.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售排行榜 DTO
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
public class SalesRankingDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 部门
     */
    private String dept;

    /**
     * 客户总数
     */
    private Integer totalCustomers;

    /**
     * 成交客户数
     */
    private Integer dealCustomers;

    /**
     * 成交率
     */
    private BigDecimal dealRate;

    /**
     * 合同总数
     */
    private Integer totalContracts;

    /**
     * 合同总金额
     */
    private BigDecimal totalAmount;

    /**
     * 排名
     */
    private Integer rank;
}
