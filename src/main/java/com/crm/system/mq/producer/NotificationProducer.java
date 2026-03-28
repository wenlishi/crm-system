package com.crm.system.mq.producer;

import com.crm.system.config.RabbitMQConfig;
import com.crm.system.model.SystemNotificationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 系统通知消息发送器
 * 
 * 使用场景：
 * 1. 客户分配通知
 * 2. 合同到期提醒
 * 3. 跟进提醒
 * 4. 任务分配通知
 * 
 * @author crm-system
 * @version 1.0
 */
@Slf4j
@Component
public class NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送客户分配通知
     * 
     * @param recipientId 接收人 ID
     * @param recipientName 接收人姓名
     * @param customerId 客户 ID
     * @param customerName 客户名称
     * @param senderId 发送人 ID（分配人）
     * @param senderName 发送人姓名
     */
    public void sendCustomerAllocationNotification(Long recipientId, String recipientName,
                                                     Long customerId, String customerName,
                                                     Long senderId, String senderName) {
        
        SystemNotificationMessage message = SystemNotificationMessage.builder()
                .notificationId(System.currentTimeMillis())
                .recipientId(recipientId)
                .recipientName(recipientName)
                .type("CUSTOMER_ALLOCATION")
                .title("客户分配通知")
                .content("您已被分配新客户：" + customerName)
                .senderId(senderId)
                .senderName(senderName)
                .sendTime(LocalDateTime.now())
                .needAck(true)
                .businessType("customer")
                .businessId(customerId)
                .build();

        sendMessage(message);
    }

    /**
     * 发送合同到期提醒
     * 
     * @param recipientId 接收人 ID（销售员）
     * @param recipientName 接收人姓名
     * @param contractId 合同 ID
     * @param contractName 合同名称
     * @param customerName 客户名称
     * @param expireDate 到期日期
     */
    public void sendContractExpireReminder(Long recipientId, String recipientName,
                                             Long contractId, String contractName,
                                             String customerName, String expireDate) {
        
        SystemNotificationMessage message = SystemNotificationMessage.builder()
                .notificationId(System.currentTimeMillis())
                .recipientId(recipientId)
                .recipientName(recipientName)
                .type("CONTRACT_EXPIRE")
                .title("合同到期提醒")
                .content("合同 " + contractName + "（" + customerName + "）将于 " + expireDate + " 到期")
                .senderId(1L)
                .senderName("系统")
                .sendTime(LocalDateTime.now())
                .needAck(false)
                .businessType("contract")
                .businessId(contractId)
                .build();

        // 发送到合同到期队列
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);

        log.info("📤 发送合同到期提醒：{}", messageId);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            RabbitMQConfig.CONTRACT_EXPIRE_ROUTING_KEY,
            message,
            correlationData
        );

        log.info("✅ 合同到期提醒发送完成：{}", messageId);
    }

    /**
     * 发送通用系统通知
     */
    public void sendGeneralNotification(Long recipientId, String recipientName,
                                         String title, String content,
                                         String type, String businessType, Long businessId) {
        
        SystemNotificationMessage message = SystemNotificationMessage.builder()
                .notificationId(System.currentTimeMillis())
                .recipientId(recipientId)
                .recipientName(recipientName)
                .type(type)
                .title(title)
                .content(content)
                .senderId(1L)
                .senderName("系统")
                .sendTime(LocalDateTime.now())
                .needAck(false)
                .businessType(businessType)
                .businessId(businessId)
                .build();

        sendMessage(message);
    }

    /**
     * 发送通知到系统通知队列
     */
    private void sendMessage(SystemNotificationMessage message) {
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);

        log.info("📤 发送系统通知：{}", messageId);
        log.info("   接收人：{} ({})", message.getRecipientName(), message.getRecipientId());
        log.info("   标题：{}", message.getTitle());
        log.info("   类型：{}", message.getType());

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
            message,
            correlationData
        );

        log.info("✅ 系统通知发送完成：{}", messageId);
    }
}
