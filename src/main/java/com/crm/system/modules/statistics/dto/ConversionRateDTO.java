package com.crm.system.modules.statistics.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 转化率分析 DTO
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
public class ConversionRateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总客户数
     */
    private Integer totalCustomers;

    /**
     * 有效客户数
     */
    private Integer validCustomers;

    /**
     * 商机总数
     */
    private Integer totalOpportunities;

    /**
     * 成交商机数
     */
    private Integer dealOpportunities;

    /**
     * 合同总数
     */
    private Integer totalContracts;

    /**
     * 成交合同数
     */
    private Integer dealContracts;

    /**
     * 客户→商机转化率
     */
    private Double customerToOpportunityRate;

    /**
     * 商机→合同转化率
     */
    private Double opportunityToContractRate;

    /**
     * 总体转化率
     */
    private Double overallConversionRate;

    /**
     * 销售漏斗各阶段数据
     */
    private List<FunnelStageDTO> funnelStages;

    /**
     * 漏斗阶段 DTO
     */
    @Data
    public static class FunnelStageDTO {
        private String stageName;
        private Integer count;
        private Double conversionRate;
    }
}
