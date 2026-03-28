package com.crm.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客户线索转换消息
 * 
 * 用于在客户状态变更时发送消息到队列
 * 
 * @author crm-system
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerConversionMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 客户 ID */
    private Long customerId;

    /** 客户名称 */
    private String customerName;

    /** 原状态 */
    private String fromStatus;

    /** 新状态 */
    private String toStatus;

    /** 操作人 ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 转换时间 */
    private LocalDateTime conversionTime;

    /** 备注 */
    private String remark;
}
