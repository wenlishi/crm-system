-- ============================================
-- CRM 系统 - 销售线索管理模块 SQL 脚本
-- ============================================
-- 创建时间：2026-03-28
-- 功能：添加独立的销售线索表，实现线索到客户的转化流程
-- ============================================

USE crm_system;

-- ============================================
-- 1. 创建销售线索表
-- ============================================

CREATE TABLE `crm_lead` (
  `lead_id` BIGINT NOT NULL COMMENT '线索 ID',
  `lead_name` VARCHAR(100) NOT NULL COMMENT '线索名称',
  `lead_type` TINYINT DEFAULT 1 COMMENT '线索类型（1 个人 2 企业）',
  `lead_status` TINYINT DEFAULT 1 COMMENT '线索状态（1 待联系 2 联系中 3 已转化 4 已关闭）',
  `lead_level` TINYINT DEFAULT 1 COMMENT '线索级别（1 普通 2 意向 3 高意向）',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '线索来源（网络营销/电话咨询/客户推荐/展会活动/其他）',
  `industry` VARCHAR(50) DEFAULT NULL COMMENT '所属行业',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `contact` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `company_name` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
  `company_scale` VARCHAR(50) DEFAULT NULL COMMENT '公司规模',
  `budget` DECIMAL(10,2) DEFAULT NULL COMMENT '预算',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `converted_customer_id` BIGINT DEFAULT NULL COMMENT '转化后的客户 ID',
  `converted_time` DATETIME DEFAULT NULL COMMENT '转化时间',
  `closed_reason` VARCHAR(200) DEFAULT NULL COMMENT '关闭原因',
  `remark` TEXT COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`lead_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`lead_status`),
  KEY `idx_level` (`lead_level`),
  KEY `idx_source` (`source`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_converted` (`converted_customer_id`),
  KEY `idx_next_follow` (`next_follow_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售线索表';

-- ============================================
-- 2. 创建线索跟进记录表
-- ============================================

CREATE TABLE `crm_lead_follow_up` (
  `follow_id` BIGINT NOT NULL COMMENT '跟进 ID',
  `lead_id` BIGINT NOT NULL COMMENT '线索 ID',
  `user_id` BIGINT NOT NULL COMMENT '跟进人 ID',
  `follow_type` TINYINT DEFAULT 1 COMMENT '跟进类型（1 电话 2 微信 3 邮件 4 面谈 5 其他）',
  `follow_content` TEXT NOT NULL COMMENT '跟进内容',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `next_follow_plan` VARCHAR(500) DEFAULT NULL COMMENT '下次跟进计划',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`follow_id`),
  KEY `idx_lead_id` (`lead_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_next_follow` (`next_follow_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线索跟进记录表';

-- ============================================
-- 3. 创建线索转化记录表
-- ============================================

CREATE TABLE `crm_lead_conversion` (
  `conversion_id` BIGINT NOT NULL COMMENT '转化 ID',
  `lead_id` BIGINT NOT NULL COMMENT '线索 ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
  `converted_by` BIGINT NOT NULL COMMENT '转化人 ID',
  `converted_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '转化时间',
  `lead_status_before` TINYINT NOT NULL COMMENT '转化前线索状态',
  `lead_level_before` TINYINT NOT NULL COMMENT '转化前线索级别',
  `customer_level_after` TINYINT DEFAULT 1 COMMENT '转化后客户级别',
  `remark` TEXT COMMENT '转化备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`conversion_id`),
  KEY `idx_lead_id` (`lead_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_converted_by` (`converted_by`),
  KEY `idx_converted_time` (`converted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线索转化记录表';

-- ============================================
-- 4. 插入测试数据
-- ============================================

-- 插入测试线索
INSERT INTO `crm_lead` (`lead_id`, `lead_name`, `lead_type`, `lead_status`, `lead_level`, `source`, `industry`, `owner_id`, `contact`, `phone`, `email`, `company_name`, `budget`, `next_follow_time`, `remark`) VALUES
(1773500000000000001, '张三', 1, 1, 2, '网络营销', '互联网', 1, '张三', '13800138001', 'zhangsan@example.com', '张三科技', 50000.00, '2026-03-29 10:00:00', '通过官网咨询'),
(1773500000000000002, '李四', 1, 2, 3, '电话咨询', '制造业', 1, '李四', '13800138002', 'lisi@example.com', '李四制造', 100000.00, '2026-03-29 14:00:00', '电话沟通意向强烈'),
(1773500000000000003, '王五', 2, 1, 1, '客户推荐', '零售业', 1, '王五', '13800138003', 'wangwu@example.com', '王五零售', 30000.00, '2026-03-30 09:00:00', '老客户推荐'),
(1773500000000000004, '赵六', 2, 2, 2, '展会活动', '金融业', 1, '赵六', '13800138004', 'zhaoliu@example.com', '赵六金融', 200000.00, '2026-03-30 15:00:00', '展会现场咨询'),
(1773500000000000005, '钱七', 1, 4, 1, '网络营销', '教育行业', 1, '钱七', '13800138005', 'qianqi@example.com', '钱七教育', NULL, NULL, '预算不足，已关闭');

-- 插入测试线索跟进记录
INSERT INTO `crm_lead_follow_up` (`follow_id`, `lead_id`, `user_id`, `follow_type`, `follow_content`, `next_follow_time`, `next_follow_plan`) VALUES
(1773500000000000001, 1773500000000000001, 1, 1, '电话联系，了解客户需求，对 CRM 系统很感兴趣，需要演示', '2026-03-29 10:00:00', '安排产品演示'),
(1773500000000000002, 1773500000000000002, 1, 4, '面谈沟通，客户预算充足，决策流程清晰，意向强烈', '2026-03-29 14:00:00', '准备报价方案'),
(1773500000000000003, 1773500000000000003, 1, 2, '微信沟通，客户需要了解产品功能，发送了产品资料', '2026-03-30 09:00:00', '跟进产品体验情况');

-- ============================================
-- 5. 查询视图
-- ============================================

-- 线索转化统计视图
CREATE OR REPLACE VIEW `v_lead_conversion_stats` AS
SELECT 
    DATE(converted_time) AS conversion_date,
    COUNT(*) AS total_converted,
    SUM(CASE WHEN lead_level_before = 3 THEN 1 ELSE 0 END) AS high_level_converted,
    SUM(CASE WHEN lead_level_before = 2 THEN 1 ELSE 0 END) AS medium_level_converted,
    SUM(CASE WHEN lead_level_before = 1 THEN 1 ELSE 0 END) AS low_level_converted,
    AVG(CASE WHEN customer_level_after IS NOT NULL THEN customer_level_after END) AS avg_customer_level
FROM crm_lead_conversion
GROUP BY DATE(converted_time)
ORDER BY conversion_date DESC;

-- 线索来源统计视图
CREATE OR REPLACE VIEW `v_lead_source_stats` AS
SELECT 
    source,
    COUNT(*) AS total_leads,
    SUM(CASE WHEN lead_status = 3 THEN 1 ELSE 0 END) AS converted_count,
    SUM(CASE WHEN lead_status = 4 THEN 1 ELSE 0 END) AS closed_count,
    ROUND(SUM(CASE WHEN lead_status = 3 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS conversion_rate
FROM crm_lead
WHERE deleted = 0
GROUP BY source
ORDER BY total_leads DESC;

-- ============================================
-- 6. 索引优化说明
-- ============================================

-- 线索表索引说明：
-- idx_owner_id: 按负责人筛选线索
-- idx_status: 按状态筛选（待联系/联系中/已转化/已关闭）
-- idx_level: 按级别筛选（普通/意向/高意向）
-- idx_source: 按来源统计
-- idx_converted: 查询转化后的线索
-- idx_next_follow: 查询待跟进线索（覆盖索引）

-- 线索跟进表索引说明：
-- idx_lead_id: 查询线索的跟进历史
-- idx_next_follow: 查询待跟进记录（覆盖索引）

-- 线索转化表索引说明：
-- idx_lead_id: 查询线索的转化记录
-- idx_customer_id: 查询客户的来源线索
-- idx_converted_time: 按转化时间统计

-- ============================================
-- 7. 数据迁移（可选）
-- ============================================

-- 如果需要将现有客户表中的线索数据迁移到线索表，可以使用以下 SQL：
-- 注意：这需要根据实际情况调整

-- INSERT INTO crm_lead (lead_id, lead_name, lead_type, lead_status, lead_level, source, owner_id, contact, phone, email, company_name, create_time)
-- SELECT 
--     customer_id,
--     customer_name,
--     customer_type,
--     CASE 
--         WHEN status = 0 THEN 4  -- 失效 → 已关闭
--         WHEN status = 1 THEN 2  -- 有效 → 联系中
--         WHEN status = 2 THEN 3  -- 已成交 → 已转化
--     END,
--     level,
--     source,
--     owner_id,
--     contact,
--     phone,
--     email,
--     company_name,
--     create_time
-- FROM crm_customer
-- WHERE level = 1  -- 只迁移普通级别的客户（作为线索）
-- AND deleted = 0;

-- ============================================
-- 完成
-- ============================================
