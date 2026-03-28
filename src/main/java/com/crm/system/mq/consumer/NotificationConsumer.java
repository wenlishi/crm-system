package com.crm.system.mq.consumer;

import com.crm.system.model.SystemNotificationMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 系统通知消息消费者
 * 
 * 监听系统通知消息，执行通知处理：
 * 1. 保存通知到数据库
 * 2. 推送实时通知（WebSocket）
 * 3. 发送邮件/短信（可选）
 * 
 * @author crm-system
 * @version 1.0
 */
@Slf4j
@Component
public class NotificationConsumer {

    /**
     * 监听系统通知队列
     * 
     * 确认机制：手动确认
     */
    @RabbitListener(queues = "${rabbitmq.queue.notification:crm.notification.queue}")
    public void handleNotification(SystemNotificationMessage message,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        String messageId = String.valueOf(System.currentTimeMillis());
        
        try {
            log.info("📥 收到系统通知消息：{}", messageId);
            log.info("   接收人：{} ({})", message.getRecipientName(), message.getRecipientId());
            log.info("   标题：{}", message.getTitle());
            log.info("   类型：{}", message.getType());
            log.info("   内容：{}", message.getContent());

            // ========== 业务处理逻辑 ==========
            
            // 1. 保存通知到数据库
            saveNotification(message);
            
            // 2. 推送实时通知（WebSocket）
            pushRealtimeNotification(message);
            
            // 3. 发送邮件/短信（如果需要）
            sendEmailOrSmsIfNeeded(message);

            // ========== 处理成功，手动确认消息 ==========
            channel.basicAck(deliveryTag, false);
            log.info("✅ 系统通知处理成功：{}", messageId);

        } catch (Exception e) {
            log.error("❌ 系统通知处理失败：{}", messageId, e);
            
            try {
                // 处理失败，拒绝消息（不重新入队）
                channel.basicNack(deliveryTag, false, false);
                log.warn("⚠️ 消息已拒绝：{}", messageId);
                
            } catch (Exception ex) {
                log.error("❌ 拒绝消息时出错：{}", messageId, ex);
            }
        }
    }

    /**
     * 监听合同到期提醒队列
     */
    @RabbitListener(queues = "${rabbitmq.queue.contract-expire:crm.contract.expire.queue}")
    public void handleContractExpire(SystemNotificationMessage message,
                                      Channel channel,
                                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        String messageId = String.valueOf(System.currentTimeMillis());
        
        try {
            log.info("📥 收到合同到期提醒：{}", messageId);
            log.info("   合同：{} ({})", message.getContent(), message.getBusinessId());
            log.info("   接收人：{} ({})", message.getRecipientName(), message.getRecipientId());

            // 保存提醒并通知销售员
            saveContractReminder(message);

            channel.basicAck(deliveryTag, false);
            log.info("✅ 合同到期提醒处理成功：{}", messageId);

        } catch (Exception e) {
            log.error("❌ 合同到期提醒处理失败：{}", messageId, e);
            
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("❌ 拒绝消息时出错：{}", messageId, ex);
            }
        }
    }

    /**
     * 保存通知到数据库
     */
    private void saveNotification(SystemNotificationMessage message) {
        log.info("💾 保存通知到数据库：");
        log.info("   通知 ID: {}", message.getNotificationId());
        log.info("   接收人：{}", message.getRecipientId());
        log.info("   标题：{}", message.getTitle());
        
        // TODO: 实际项目中，这里应该调用 Service 层保存
        // notificationService.save(message);
    }

    /**
     * 推送实时通知
     */
    private void pushRealtimeNotification(SystemNotificationMessage message) {
        log.info("📱 推送实时通知（WebSocket）：");
        log.info("   用户 ID: {}", message.getRecipientId());
        
        // TODO: 实际项目中，使用 WebSocket 推送
        // simpMessagingTemplate.convertAndSendToUser(
        //     message.getRecipientId().toString(),
        //     "/queue/notifications",
        //     message
        // );
    }

    /**
     * 发送邮件或短信
     */
    private void sendEmailOrSmsIfNeeded(SystemNotificationMessage message) {
        // 重要通知才发送邮件/短信
        if ("IMPORTANT".equals(message.getType())) {
            log.info("📧 发送重要通知邮件/短信");
            // TODO: 调用邮件/短信服务
        }
    }

    /**
     * 保存合同到期提醒
     */
    private void saveContractReminder(SystemNotificationMessage message) {
        log.info("💾 保存合同到期提醒：");
        log.info("   合同 ID: {}", message.getBusinessId());
        log.info("   销售员：{}", message.getRecipientId());
        
        // TODO: 实际项目中，调用 Service 层保存
        // contractReminderService.save(message);
    }
}
