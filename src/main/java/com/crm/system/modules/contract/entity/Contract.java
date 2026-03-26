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
     * 客户名称（前端使用，不映射到数据库）
     */
    @TableField(exist = false)
    private String customerName;

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
     * 生效日期（前端使用，与 startDate 相同）
     */
    @TableField(exist = false)
    private LocalDate effectiveDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 到期日期（前端使用，与 endDate 相同）
     */
    @TableField(exist = false)
    private LocalDate expiryDate;

    /**
     * 合同状态（0 草稿 1 待审核 2 已审核 3 执行中 4 已完成 5 已终止）
     */
    private Integer status = 0;

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
