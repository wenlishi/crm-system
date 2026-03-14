package com.crm.system.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公海池实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@ToString(exclude = {"createTime", "updateTime"})
@TableName("crm_customer_pool")
public class CustomerPool implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公海池 ID
     */
    @TableId(value = "pool_id", type = IdType.ASSIGN_ID)
    private Long poolId;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 原负责人 ID
     */
    private Long previousOwnerId;

    /**
     * 原负责人姓名
     */
    private String previousOwnerName;

    /**
     * 掉入公海原因
     * （1 超期未跟进 2 主动释放 3 离职交接 4 其他）
     */
    private Integer dropReason;

    /**
     * 掉入公海时间
     */
    private LocalDateTime dropTime;

    /**
     * 状态（0 在池中 1 已领取）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

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
