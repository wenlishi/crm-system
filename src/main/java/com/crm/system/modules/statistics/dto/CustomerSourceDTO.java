package com.crm.system.modules.statistics.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户来源分析 DTO
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
public class CustomerSourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 来源名称
     */
    private String source;

    /**
     * 客户数量
     */
    private Integer count;

    /**
     * 占比
     */
    private Double percentage;

    /**
     * 成交数量
     */
    private Integer dealCount;

    /**
     * 成交率
     */
    private Double dealRate;

    /**
     * 来源列表（用于饼图）
     */
    private List<CustomerSourceDTO> sources;
}
