package com.crm.system.mq.consumer;

import com.crm.system.model.CustomerConversionMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 客户线索转换消息消费者
 * 
 * 监听客户状态变更消息，执行后续处理：
 * 1. 记录客户状态变更日志
 * 2. 发送通知给相关人员
 * 3. 更新统计数据
 * 
 * @author crm-system
 * @version 1.0
 */
@Slf4j
@Component
public class CustomerConversionConsumer {

    /**
     * 监听客户线索转换队列
     * 
     * 确认机制：手动确认（Manual Acknowledge）
     * - 处理成功：basicAck（确认消息）
     * - 处理失败：basicNack（拒绝消息，重新入队或进入死信队列）
     * 
     * @param message 客户转换消息
     * @param channel RabbitMQ 通道
     * @param deliveryTag 消息投递标签
     */
    @RabbitListener(queues = "${rabbitmq.queue.customer-conversion:crm.customer.conversion.queue}")
    public void handleCustomerConversion(CustomerConversionMessage message,
                                          Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        String messageId = String.valueOf(System.currentTimeMillis());
        
        try {
            log.info("📥 收到客户转换消息：{}", messageId);
            log.info("   客户：{} ({})", message.getCustomerName(), message.getCustomerId());
            log.info("   状态：{} → {}", message.getFromStatus(), message.getToStatus());
            log.info("   操作人：{} ({})", message.getOperatorName(), message.getOperatorId());

            // ========== 业务处理逻辑 ==========
            
            // 1. 记录客户状态变更日志
            logConversionHistory(message);
            
            // 2. 更新客户统计数据
            updateCustomerStatistics(message);
            
            // 3. 发送通知给相关人员（如果需要）
            sendNotificationIfNeeded(message);

            // ========== 处理成功，手动确认消息 ==========
            channel.basicAck(deliveryTag, false);
            log.info("✅ 客户转换消息处理成功：{}", messageId);

        } catch (Exception e) {
            log.error("❌ 客户转换消息处理失败：{}", messageId, e);
            
            try {
                // 处理失败，拒绝消息
                // requeue = false：不重新入队，进入死信队列
                // requeue = true：重新入队，再次消费
                channel.basicNack(deliveryTag, false, false);
                log.warn("⚠️ 消息已拒绝，进入死信队列：{}", messageId);
                
            } catch (Exception ex) {
                log.error("❌ 拒绝消息时出错：{}", messageId, ex);
            }
        }
    }

    /**
     * 记录转换历史
     */
    private void logConversionHistory(CustomerConversionMessage message) {
        log.info("📝 记录客户状态变更历史：");
        log.info("   客户 ID: {}", message.getCustomerId());
        log.info("   变更前：{}", message.getFromStatus());
        log.info("   变更后：{}", message.getToStatus());
        log.info("   变更时间：{}", message.getConversionTime());
        log.info("   备注：{}", message.getRemark());
        
        // TODO: 实际项目中，这里应该调用 Service 层保存到数据库
        // conversionHistoryService.save(message);
    }

    /**
     * 更新客户统计数据
     */
    private void updateCustomerStatistics(CustomerConversionMessage message) {
        log.info("📊 更新客户统计数据：");
        log.info("   客户类型统计更新");
        log.info("   销售漏斗统计更新");
        
        // TODO: 实际项目中，这里应该调用 Service 层更新统计
        // statisticsService.updateCustomerStats(message);
    }

    /**
     * 发送通知（如果需要）
     */
    private void sendNotificationIfNeeded(CustomerConversionMessage message) {
        // 如果是成交客户，发送通知给销售员
        if ("DEAL".equals(message.getToStatus())) {
            log.info("🎉 客户成交，发送喜报通知！");
            // TODO: 调用通知服务
            // notificationService.sendDealNotification(message);
        }
    }
}
