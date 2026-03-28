package com.crm.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统通知消息
 * 
 * 用于发送系统通知到队列
 * 
 * @author crm-system
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemNotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通知 ID */
    private Long notificationId;

    /** 接收人 ID */
    private Long recipientId;

    /** 接收人姓名 */
    private String recipientName;

    /** 通知类型 */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 发送人 ID */
    private Long senderId;

    /** 发送人姓名 */
    private String senderName;

    /** 发送时间 */
    private LocalDateTime sendTime;

    /** 是否需要确认 */
    private Boolean needAck;

    /** 业务类型（customer/contract/follow 等） */
    private String businessType;

    /** 业务 ID */
    private Long businessId;
}
