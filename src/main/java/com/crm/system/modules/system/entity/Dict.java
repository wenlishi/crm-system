package com.crm.system.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典实体类
 * 
 * @author wenlishi
 * @since 2026-03-15
 */
@Data
@TableName("sys_dict")
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典 ID
     */
    @TableId(value = "dict_id", type = IdType.ASSIGN_ID)
    private Long dictId;

    /**
     * 字典类型（如：customer_level）
     */
    private String dictType;

    /**
     * 字典标签（如：普通）
     */
    private String dictLabel;

    /**
     * 字典值（如：1）
     */
    private String dictValue;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态（0 禁用 1 正常）
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
}
