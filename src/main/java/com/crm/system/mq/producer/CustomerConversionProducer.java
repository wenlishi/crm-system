package com.crm.system.mq.producer;

import com.crm.system.config.RabbitMQConfig;
import com.crm.system.model.CustomerConversionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 客户线索转换消息发送器
 * 
 * 使用场景：
 * 1. 客户状态变更（潜在客户 → 意向客户 → 成交客户）
 * 2. 客户分配（公海池 → 销售员）
 * 3. 客户转移（销售员 A → 销售员 B）
 * 
 * @author crm-system
 * @version 1.0
 */
@Slf4j
@Component
public class CustomerConversionProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送客户状态转换消息
     * 
     * @param customerId 客户 ID
     * @param customerName 客户名称
     * @param fromStatus 原状态
     * @param toStatus 新状态
     * @param operatorId 操作人 ID
     * @param operatorName 操作人姓名
     * @param remark 备注
     */
    public void sendConversionMessage(Long customerId, String customerName,
                                       String fromStatus, String toStatus,
                                       Long operatorId, String operatorName,
                                       String remark) {
        
        // 构建消息
        CustomerConversionMessage message = CustomerConversionMessage.builder()
                .customerId(customerId)
                .customerName(customerName)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .operatorId(operatorId)
                .operatorName(operatorName)
                .conversionTime(LocalDateTime.now())
                .remark(remark)
                .build();

        // 生成消息 ID（用于追踪）
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);

        log.info("📤 发送客户转换消息：{}", messageId);
        log.info("   客户：{} ({})", customerName, customerId);
        log.info("   状态：{} → {}", fromStatus, toStatus);
        log.info("   操作人：{} ({})", operatorName, operatorId);

        // 发送消息（启用确认机制）
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.CUSTOMER_EXCHANGE,
            RabbitMQConfig.CUSTOMER_CONVERSION_ROUTING_KEY,
            message,
            correlationData
        );

        log.info("✅ 客户转换消息发送完成：{}", messageId);
    }

    /**
     * 发送客户分配消息
     */
    public void sendAllocationMessage(Long customerId, String customerName,
                                       Long fromOwnerId, String fromOwnerName,
                                       Long toOwnerId, String toOwnerName) {
        
        CustomerConversionMessage message = CustomerConversionMessage.builder()
                .customerId(customerId)
                .customerName(customerName)
                .fromStatus("OWNER_" + fromOwnerId)
                .toStatus("OWNER_" + toOwnerId)
                .operatorId(toOwnerId)
                .operatorName(toOwnerName)
                .conversionTime(LocalDateTime.now())
                .remark("客户分配：" + fromOwnerName + " → " + toOwnerName)
                .build();

        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);

        log.info("📤 发送客户分配消息：{}", messageId);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.CUSTOMER_EXCHANGE,
            RabbitMQConfig.CUSTOMER_CONVERSION_ROUTING_KEY,
            message,
            correlationData
        );

        log.info("✅ 客户分配消息发送完成：{}", messageId);
    }
}
