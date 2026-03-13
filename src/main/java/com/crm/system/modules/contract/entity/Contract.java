package com.crm.system.modules.contract.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@ToString(exclude = {"createTime", "updateTime"})
@TableName("crm_contract")
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同 ID
     */
    @TableId(value = "contract_id", type = IdType.ASSIGN_ID)
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 商机 ID
     */
    private Long opportunityId;

    /**
     * 合同金额
     */
    private BigDecimal amount;

    /**
     * 签订日期
     */
    private LocalDate signDate;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 合同状态（1 草稿 2 待审核 3 已审核 4 执行中 5 已完成 6 已终止）
     */
    private Integer status;

    /**
     * 合同文件 URL
     */
    private String fileUrl;

    /**
     * 负责人 ID
     */
    private Long ownerId;

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
