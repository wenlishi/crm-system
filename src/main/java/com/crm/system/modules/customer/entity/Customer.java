package com.crm.system.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@ToString(exclude = {"createTime", "updateTime"})
@TableName("crm_customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户 ID
     */
    @TableId(value = "customer_id", type = IdType.ASSIGN_ID)
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 客户类型（1 个人 2 企业）
     */
    private Integer customerType;

    /**
     * 客户等级（1 普通 2 VIP 3 重要）
     */
    private Integer level;

    /**
     * 客户来源
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
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 微信
     */
    private String wechat;

    /**
     * QQ
     */
    private String qq;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 公司规模
     */
    private String companyScale;

    /**
     * 状态（0 失效 1 有效 2 已成交）
     */
    private Integer status;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

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
