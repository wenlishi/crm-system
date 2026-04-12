package com.crm.system.modules.lead.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售线索实体类
 * 
 * @author wenlishi
 * @since 2026-03-28
 */
@Data
@ToString(exclude = {"createTime", "updateTime"})
@TableName("crm_lead")
public class Lead implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 线索 ID
     */
    @TableId(value = "lead_id", type = IdType.ASSIGN_ID)
    private Long leadId;

    /**
     * 线索名称
     */
    private String leadName;

    /**
     * 线索类型（1 个人 2 企业）
     */
    private Integer leadType;

    /**
     * 线索状态（1 待联系 2 联系中 3 已转化 4 已关闭）
     */
    private Integer leadStatus;

    /**
     * 线索级别（1 普通 2 意向 3 高意向）
     */
    private Integer leadLevel;

    /**
     * 线索来源
     */
    private String source;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 负责人 ID
     */
    private Long ownerId;

    /**
     * 联系人
     */
    private String contact;

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
     * 公司规模
     */
    private String companyScale;

    /**
     * 预算
     */
    private BigDecimal budget;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

    /**
     * 转化后的客户 ID
     */
    private Long convertedCustomerId;

    /**
     * 转化时间
     */
    private LocalDateTime convertedTime;

    /**
     * 关闭原因
     */
    private String closedReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
