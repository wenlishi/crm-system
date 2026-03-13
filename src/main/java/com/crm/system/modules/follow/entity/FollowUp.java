package com.crm.system.modules.follow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 跟进记录实体类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@Data
@ToString(exclude = {"createTime", "updateTime"})
@TableName("crm_follow_up")
public class FollowUp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 跟进 ID
     */
    @TableId(value = "follow_id", type = IdType.ASSIGN_ID)
    private Long followId;

    /**
     * 客户 ID
     */
    private Long customerId;

    /**
     * 跟进人 ID
     */
    private Long userId;

    /**
     * 跟进方式（1 电话 2 微信 3 邮件 4 面谈 5 其他）
     */
    private Integer followType;

    /**
     * 跟进内容
     */
    private String content;

    /**
     * 下一步计划
     */
    private String nextPlan;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextFollowTime;

    /**
     * 附件地址
     */
    private String attachmentUrl;

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
