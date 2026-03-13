-- ============================================
-- CRM 客户管理系统 - 数据库初始化脚本
-- ============================================
-- 创建时间：2026-03-14
-- 数据库：crm_system
-- 字符集：utf8mb4
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS crm_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE crm_system;

-- ============================================
-- 1. 系统管理模块
-- ============================================

-- 用户表
CREATE TABLE `sys_user` (
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `dept_id` BIGINT DEFAULT NULL COMMENT '部门 ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0 禁用 1 正常）',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0 未删除 1 已删除）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE `sys_role` (
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0 禁用 1 正常）',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE `sys_user_role` (
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 数据字典表
CREATE TABLE `sys_dict` (
  `dict_id` BIGINT NOT NULL COMMENT '字典 ID',
  `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型',
  `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
  `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`dict_id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- ============================================
-- 2. 客户管理模块
-- ============================================

-- 客户表
CREATE TABLE `crm_customer` (
  `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `customer_type` TINYINT DEFAULT 1 COMMENT '客户类型（1 个人 2 企业）',
  `level` TINYINT DEFAULT 1 COMMENT '客户等级（1 普通 2 VIP 3 重要）',
  `source` VARCHAR(50) DEFAULT NULL COMMENT '客户来源',
  `industry` VARCHAR(50) DEFAULT NULL COMMENT '所属行业',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '地址',
  `wechat` VARCHAR(50) DEFAULT NULL COMMENT '微信',
  `qq` VARCHAR(20) DEFAULT NULL COMMENT 'QQ',
  `company_name` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
  `company_scale` VARCHAR(50) DEFAULT NULL COMMENT '公司规模',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0 失效 1 有效 2 已成交）',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `remark` TEXT COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`customer_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_level_status` (`level`, `status`),
  KEY `idx_next_follow` (`next_follow_time`, `status`),
  KEY `idx_customer_name` (`customer_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ============================================
-- 3. 跟进管理模块
-- ============================================

-- 跟进记录表
CREATE TABLE `crm_follow_up` (
  `follow_id` BIGINT NOT NULL COMMENT '跟进 ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
  `user_id` BIGINT NOT NULL COMMENT '跟进人 ID',
  `follow_type` TINYINT DEFAULT 1 COMMENT '跟进方式（1 电话 2 微信 3 邮件 4 面谈 5 其他）',
  `content` TEXT NOT NULL COMMENT '跟进内容',
  `next_plan` TEXT COMMENT '下一步计划',
  `next_follow_time` DATETIME DEFAULT NULL COMMENT '下次跟进时间',
  `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件地址',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`follow_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_next_follow` (`next_follow_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跟进记录表';

-- ============================================
-- 4. 商机管理模块
-- ============================================

-- 商机表
CREATE TABLE `crm_opportunity` (
  `opportunity_id` BIGINT NOT NULL COMMENT '商机 ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
  `opportunity_name` VARCHAR(200) NOT NULL COMMENT '商机名称',
  `stage` TINYINT DEFAULT 1 COMMENT '商机阶段（1 初步接触 2 需求确认 3 方案报价 4 谈判 5 成交）',
  `expected_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '预计金额',
  `actual_amount` DECIMAL(12,2) DEFAULT NULL COMMENT '实际金额',
  `probability` INT DEFAULT NULL COMMENT '成功概率（%）',
  `expected_close_date` DATE DEFAULT NULL COMMENT '预计成交日期',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0 失败 1 进行中 2 成交 3 无效）',
  `remark` TEXT COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`opportunity_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_stage` (`stage`),
  KEY `idx_status` (`status`),
  KEY `idx_expected_close` (`expected_close_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商机表';

-- ============================================
-- 5. 合同管理模块
-- ============================================

-- 合同表
CREATE TABLE `crm_contract` (
  `contract_id` BIGINT NOT NULL COMMENT '合同 ID',
  `contract_no` VARCHAR(50) NOT NULL COMMENT '合同编号',
  `contract_name` VARCHAR(200) NOT NULL COMMENT '合同名称',
  `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
  `opportunity_id` BIGINT DEFAULT NULL COMMENT '商机 ID',
  `amount` DECIMAL(12,2) NOT NULL COMMENT '合同金额',
  `sign_date` DATE DEFAULT NULL COMMENT '签订日期',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `status` TINYINT DEFAULT 1 COMMENT '合同状态（1 草稿 2 待审核 3 已审核 4 执行中 5 已完成 6 已终止）',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '合同文件 URL',
  `owner_id` BIGINT DEFAULT NULL COMMENT '负责人 ID',
  `remark` TEXT COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除（0 未删除 1 已删除）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`contract_id`),
  UNIQUE KEY `uk_contract_no` (`contract_no`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_opportunity_id` (`opportunity_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sign_date` (`sign_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员用户（密码：admin123，BCrypt 加密）
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `email`, `phone`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@crm.com', '13800138000', 1);

-- 插入默认角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_code`, `description`) VALUES
(1, '超级管理员', 'super_admin', '系统超级管理员'),
(2, '销售经理', 'sales_manager', '销售部门经理'),
(3, '销售专员', 'sales', '普通销售人员');

-- 绑定管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 插入数据字典
INSERT INTO `sys_dict` (`dict_id`, `dict_type`, `dict_label`, `dict_value`, `sort_order`) VALUES
(1, 'customer_type', '个人', '1', 1),
(2, 'customer_type', '企业', '2', 2),
(3, 'customer_level', '普通', '1', 1),
(4, 'customer_level', 'VIP', '2', 2),
(5, 'customer_level', '重要', '3', 3),
(6, 'follow_type', '电话', '1', 1),
(7, 'follow_type', '微信', '2', 2),
(8, 'follow_type', '邮件', '3', 3),
(9, 'follow_type', '面谈', '4', 4),
(10, 'opportunity_stage', '初步接触', '1', 1),
(11, 'opportunity_stage', '需求确认', '2', 2),
(12, 'opportunity_stage', '方案报价', '3', 3),
(13, 'opportunity_stage', '谈判', '4', 4),
(14, 'opportunity_stage', '成交', '5', 5);

-- ============================================
-- 结束
-- ============================================
