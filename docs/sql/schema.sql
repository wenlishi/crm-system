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
  `data_scope` TINYINT DEFAULT 2 COMMENT '数据权限范围（1 全部 2 本部门及以下 3 本部门 4 仅本人）',
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
-- 6. 权限管理模块
-- ============================================

-- 权限表
CREATE TABLE `sys_permission` (
  `permission_id` BIGINT NOT NULL COMMENT '权限 ID',
  `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码（如：customer:add, customer:edit）',
  `type` TINYINT DEFAULT 1 COMMENT '权限类型（1 菜单 2 按钮 3 接口）',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限 ID',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路径',
  `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0 禁用 1 正常）',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`permission_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE `sys_role_permission` (
  `role_id` BIGINT NOT NULL COMMENT '角色 ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 操作日志表
CREATE TABLE `sys_operation_log` (
  `log_id` BIGINT NOT NULL COMMENT '日志 ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
  `type` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '操作描述',
  `method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
  `url` VARCHAR(200) DEFAULT NULL COMMENT '请求 URL',
  `params` TEXT COMMENT '请求参数',
  `result` VARCHAR(500) DEFAULT NULL COMMENT '响应结果',
  `duration` BIGINT DEFAULT NULL COMMENT '执行时长（毫秒）',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP 地址',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '操作地点',
  `browser` VARCHAR(50) DEFAULT NULL COMMENT '浏览器',
  `os` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
  `status` TINYINT DEFAULT 1 COMMENT '操作状态（0 失败 1 成功）',
  `error_msg` TEXT COMMENT '错误信息',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_module` (`module`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 公海池表
CREATE TABLE `crm_customer_pool` (
  `pool_id` BIGINT NOT NULL COMMENT '公海池 ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户 ID',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `previous_owner_id` BIGINT DEFAULT NULL COMMENT '原负责人 ID',
  `previous_owner_name` VARCHAR(50) DEFAULT NULL COMMENT '原负责人姓名',
  `drop_reason` TINYINT DEFAULT 1 COMMENT '掉入公海原因（1 超期未跟进 2 主动释放 3 离职交接 4 其他）',
  `drop_time` DATETIME DEFAULT NULL COMMENT '掉入公海时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态（0 在池中 1 已领取）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`pool_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_drop_time` (`drop_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公海池表';

-- 文件管理表
CREATE TABLE `sys_file` (
  `file_id` BIGINT NOT NULL COMMENT '文件 ID',
  `file_name` VARCHAR(100) NOT NULL COMMENT '文件名称',
  `original_name` VARCHAR(200) DEFAULT NULL COMMENT '原始文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
  `file_url` VARCHAR(500) NOT NULL COMMENT '文件 URL',
  `file_type` VARCHAR(20) DEFAULT NULL COMMENT '文件类型（image/pdf/doc/xls 等）',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
  `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME 类型',
  `biz_type` VARCHAR(50) DEFAULT NULL COMMENT '关联业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '关联业务 ID',
  `upload_user_id` BIGINT DEFAULT NULL COMMENT '上传用户 ID',
  `upload_user_name` VARCHAR(50) DEFAULT NULL COMMENT '上传用户名',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '文件描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态（0 禁用 1 正常）',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`file_id`),
  KEY `idx_biz` (`biz_type`, `biz_id`),
  KEY `idx_upload_user` (`upload_user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件管理表';

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

-- 插入权限数据
-- 系统管理菜单
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES
(1, '系统管理', 'system', 1, 0, '/system', 'setting', 100, 1),
(2, '用户管理', 'system:user', 1, 1, '/system/user', 'user', 101, 1),
(3, '用户查询', 'system:user:query', 3, 2, NULL, NULL, 102, 1),
(4, '用户新增', 'system:user:add', 3, 2, NULL, NULL, 103, 1),
(5, '用户修改', 'system:user:edit', 3, 2, NULL, NULL, 104, 1),
(6, '用户删除', 'system:user:delete', 3, 2, NULL, NULL, 105, 1),
(7, '角色管理', 'system:role', 1, 1, '/system/role', 'peoples', 106, 1),
(8, '角色查询', 'system:role:query', 3, 7, NULL, NULL, 107, 1),
(9, '角色新增', 'system:role:add', 3, 7, NULL, NULL, 108, 1),
(10, '角色修改', 'system:role:edit', 3, 7, NULL, NULL, 109, 1),
(11, '角色删除', 'system:role:delete', 3, 7, NULL, NULL, 110, 1),
(12, '权限管理', 'system:permission', 1, 1, '/system/permission', 'lock', 111, 1),
(13, '权限查询', 'system:permission:query', 3, 12, NULL, NULL, 112, 1),
(14, '权限新增', 'system:permission:add', 3, 12, NULL, NULL, 113, 1),
(15, '权限修改', 'system:permission:edit', 3, 12, NULL, NULL, 114, 1),
(16, '权限删除', 'system:permission:delete', 3, 12, NULL, NULL, 115, 1);

-- 客户管理菜单
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES
(20, '客户管理', 'customer', 1, 0, '/customer', 'contact', 1, 1),
(21, '客户列表', 'customer:list', 1, 20, '/customer/list', 'list', 2, 1),
(22, '客户查询', 'customer:query', 3, 21, NULL, NULL, 3, 1),
(23, '客户新增', 'customer:add', 3, 21, NULL, NULL, 4, 1),
(24, '客户修改', 'customer:edit', 3, 21, NULL, NULL, 5, 1),
(25, '客户删除', 'customer:delete', 3, 21, NULL, NULL, 6, 1),
(26, '跟进记录', 'customer:follow', 1, 20, '/customer/follow', 'document', 7, 1),
(27, '跟进查询', 'customer:follow:query', 3, 26, NULL, NULL, 8, 1),
(28, '跟进新增', 'customer:follow:add', 3, 26, NULL, NULL, 9, 1),
(29, '跟进修改', 'customer:follow:edit', 3, 26, NULL, NULL, 10, 1),
(30, '跟进删除', 'customer:follow:delete', 3, 26, NULL, NULL, 11, 1);

-- 商机管理菜单
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES
(30, '商机管理', 'opportunity', 1, 0, '/opportunity', 'chart', 2, 1),
(31, '商机列表', 'opportunity:list', 1, 30, '/opportunity/list', 'list', 3, 1),
(32, '商机查询', 'opportunity:query', 3, 31, NULL, NULL, 4, 1),
(33, '商机新增', 'opportunity:add', 3, 31, NULL, NULL, 5, 1),
(34, '商机修改', 'opportunity:edit', 3, 31, NULL, NULL, 6, 1),
(35, '商机删除', 'opportunity:delete', 3, 31, NULL, NULL, 7, 1),
(36, '销售漏斗', 'opportunity:funnel', 2, 30, '/opportunity/funnel', 'funnel', 8, 1);

-- 合同管理菜单
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES
(40, '合同管理', 'contract', 1, 0, '/contract', 'file', 3, 1),
(41, '合同列表', 'contract:list', 1, 40, '/contract/list', 'list', 4, 1),
(42, '合同查询', 'contract:query', 3, 41, NULL, NULL, 5, 1),
(43, '合同新增', 'contract:add', 3, 41, NULL, NULL, 6, 1),
(44, '合同修改', 'contract:edit', 3, 41, NULL, NULL, 7, 1),
(45, '合同删除', 'contract:delete', 3, 41, NULL, NULL, 8, 1),
(46, '合同统计', 'contract:stats', 2, 40, '/contract/stats', 'chart', 9, 1);

-- 统计报表菜单
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `parent_id`, `path`, `icon`, `sort_order`, `status`) VALUES
(50, '统计报表', 'statistics', 1, 0, '/statistics', 'dashboard', 4, 1),
(51, '数据统计', 'statistics:dashboard', 2, 50, '/statistics/dashboard', 'dashboard', 5, 1),
(52, '客户分析', 'statistics:customer', 2, 50, '/statistics/customer', 'chart', 6, 1),
(53, '销售分析', 'statistics:sales', 2, 50, '/statistics/sales', 'line', 7, 1);

-- 分配超级管理员所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, permission_id FROM sys_permission;

-- 分配销售经理部分权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 20), (2, 21), (2, 22), (2, 23), (2, 24), (2, 25),
(2, 26), (2, 27), (2, 28), (2, 29), (2, 30),
(2, 31), (2, 32), (2, 33), (2, 34), (2, 35), (2, 36),
(2, 40), (2, 41), (2, 42), (2, 43), (2, 44), (2, 45), (2, 46),
(2, 50), (2, 51), (2, 52), (2, 53);

-- 分配销售专员基础权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(3, 20), (3, 21), (3, 22), (3, 23), (3, 24),
(3, 26), (3, 27), (3, 28), (3, 29),
(3, 30), (3, 31), (3, 32), (3, 33), (3, 34),
(3, 40), (3, 41), (3, 42), (3, 43), (3, 44),
(3, 50), (3, 51);

-- ============================================
-- 结束
-- ============================================
