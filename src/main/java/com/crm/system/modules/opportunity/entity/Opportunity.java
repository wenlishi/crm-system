package com.crm.system.modules.opportunity.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@ToString(exclude = {"createTime", "updateTime"})
@TableName("crm_opportunity")
public class Opportunity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商机 ID
     */
    @TableId(value = "opportunity_id", type = IdType.ASSIGN_ID)
    private Long opportunityId;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 商机名称
     */
    private String opportunityName;

    /**
     * 商机阶段（1 初步接触 2 需求确认 3 方案报价 4 谈判 5 成交）
     */
    private Integer stage;

    /**
     * 预计金额
     */
    private BigDecimal expectedAmount;

    /**
     * 实际金额
     */
    private BigDecimal actualAmount;

    /**
     * 成功概率（%）
     */
    private Integer probability;

    /**
     * 预计成交日期
     */
    private LocalDate expectedCloseDate;

    /**
     * 负责人 ID
     */
    private Long ownerId;

    /**
     * 状态（0 失败 1 进行中 2 成交 3 无效）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除（0 未删除 1 已删除）
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
