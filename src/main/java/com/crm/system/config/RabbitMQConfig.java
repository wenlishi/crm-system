package com.crm.system.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类
 * 
 * 功能：
 * 1. 客户线索转换队列 - 处理客户状态变更（潜在客户 → 意向客户 → 成交客户）
 * 2. 系统通知队列 - 发送系统通知（客户分配通知、合同到期提醒等）
 * 
 * @author crm-system
 * @version 1.0
 */
@Configuration
public class RabbitMQConfig {

    // ============================================
    // 常量定义 - 交换机、队列、路由键
    // ============================================
    
    /** 客户线索转换交换机 */
    public static final String CUSTOMER_EXCHANGE = "crm.customer.exchange";
    
    /** 客户线索转换队列 */
    public static final String CUSTOMER_CONVERSION_QUEUE = "crm.customer.conversion.queue";
    
    /** 客户线索转换路由键 */
    public static final String CUSTOMER_CONVERSION_ROUTING_KEY = "customer.conversion";
    
    /** 系统通知交换机 */
    public static final String NOTIFICATION_EXCHANGE = "crm.notification.exchange";
    
    /** 系统通知队列 */
    public static final String NOTIFICATION_QUEUE = "crm.notification.queue";
    
    /** 系统通知路由键 */
    public static final String NOTIFICATION_ROUTING_KEY = "notification.system";
    
    /** 合同到期提醒队列 */
    public static final String CONTRACT_EXPIRE_QUEUE = "crm.contract.expire.queue";
    
    /** 合同到期提醒路由键 */
    public static final String CONTRACT_EXPIRE_ROUTING_KEY = "contract.expire";

    // ============================================
    // 交换机配置
    // ============================================

    /**
     * 客户线索转换交换机（Topic 类型）
     * 支持灵活的路由规则
     */
    @Bean
    public Exchange customerExchange() {
        return ExchangeBuilder.topicExchange(CUSTOMER_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 系统通知交换机（Topic 类型）
     */
    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder.topicExchange(NOTIFICATION_EXCHANGE)
                .durable(true)
                .build();
    }

    // ============================================
    // 队列配置
    // ============================================

    /**
     * 客户线索转换队列
     * 配置：
     * - 持久化：true（重启后数据不丢失）
     * - 自动删除：false
     * - 死信队列：配置了死信处理
     */
    @Bean
    public Queue customerConversionQueue() {
        return QueueBuilder.durable(CUSTOMER_CONVERSION_QUEUE)
                .withArgument("x-message-ttl", 60000) // 消息 TTL：60 秒
                .withArgument("x-dead-letter-exchange", NOTIFICATION_EXCHANGE) // 死信交换机
                .withArgument("x-dead-letter-routing-key", "customer.conversion.failed") // 死信路由键
                .build();
    }

    /**
     * 系统通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-message-ttl", 300000) // 消息 TTL：5 分钟
                .build();
    }

    /**
     * 合同到期提醒队列
     */
    @Bean
    public Queue contractExpireQueue() {
        return QueueBuilder.durable(CONTRACT_EXPIRE_QUEUE)
                .withArgument("x-message-ttl", 600000) // 消息 TTL：10 分钟
                .build();
    }

    // ============================================
    // 绑定配置
    // ============================================

    /**
     * 绑定客户线索转换队列到交换机
     */
    @Bean
    public Binding customerConversionBinding(Queue customerConversionQueue, Exchange customerExchange) {
        return BindingBuilder.bind(customerConversionQueue)
                .to(customerExchange)
                .with(CUSTOMER_CONVERSION_ROUTING_KEY)
                .noargs();
    }

    /**
     * 绑定系统通知队列到交换机
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, Exchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(NOTIFICATION_ROUTING_KEY)
                .noargs();
    }

    /**
     * 绑定合同到期提醒队列到交换机
     */
    @Bean
    public Binding contractExpireBinding(Queue contractExpireQueue, Exchange notificationExchange) {
        return BindingBuilder.bind(contractExpireQueue)
                .to(notificationExchange)
                .with(CONTRACT_EXPIRE_ROUTING_KEY)
                .noargs();
    }

    // ============================================
    // RabbitTemplate 配置
    // ============================================

    /**
     * JSON 消息转换器
     * 自动将 Java 对象转换为 JSON 格式发送
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     * 用于发送消息
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        
        // 启用确认机制（Publisher Confirm）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("✅ 消息发送成功：" + correlationData);
            } else {
                System.err.println("❌ 消息发送失败：" + cause);
            }
        });
        
        // 启用返回机制（Return Callback）
        rabbitTemplate.setReturnsCallback(returned -> {
            System.err.println("⚠️ 消息被退回：" + returned.getMessage());
            System.err.println("   回复码：" + returned.getReplyCode());
            System.err.println("   回复文本：" + returned.getReplyText());
        });
        
        return rabbitTemplate;
    }
}
