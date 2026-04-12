-- ============================================
-- CRM 系统 - 测试数据初始化脚本
-- ============================================
-- 创建时间：2026-03-28
-- 功能：添加测试用户和基础数据
-- ============================================

USE crm_system;

-- ============================================
-- 1. 插入测试用户
-- ============================================

-- 密码都是 admin123，使用 BCrypt 加密
-- BCrypt hash for 'admin123': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `email`, `phone`, `status`, `deleted`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'admin@crm.com', '13800138000', 1, 0),
(2, 'user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售员 A', 'user1@crm.com', '13800138001', 1, 0),
(3, 'user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '销售员 B', 'user2@crm.com', '13800138002', 1, 0);

-- ============================================
-- 2. 插入测试角色
-- ============================================

INSERT INTO `sys_role` (`role_id`, `role_name`, `role_code`, `description`, `status`, `deleted`) VALUES
(1, '超级管理员', 'ADMIN', '拥有所有权限', 1, 0),
(2, '销售经理', 'MANAGER', '管理销售团队', 1, 0),
(3, '销售员', 'SALES', '普通销售人员', 1, 0);

-- ============================================
-- 3. 插入用户角色关联
-- ============================================

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin 是超级管理员
(2, 3),  -- user1 是销售员
(3, 3);  -- user2 是销售员

-- ============================================
-- 4. 插入测试权限
-- ============================================

INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_code`, `type`, `path`, `status`, `deleted`) VALUES
(1, '查看客户', 'customer:view', 'permission', NULL, 1, 0),
(2, '添加客户', 'customer:add', 'permission', NULL, 1, 0),
(3, '编辑客户', 'customer:edit', 'permission', NULL, 1, 0),
(4, '删除客户', 'customer:delete', 'permission', NULL, 1, 0),
(5, '查看线索', 'lead:view', 'permission', NULL, 1, 0),
(6, '添加线索', 'lead:add', 'permission', NULL, 1, 0),
(7, '编辑线索', 'lead:edit', 'permission', NULL, 1, 0),
(8, '删除线索', 'lead:delete', 'permission', NULL, 1, 0),
(9, '线索转化', 'lead:convert', 'permission', NULL, 1, 0);

-- ============================================
-- 5. 插入角色权限关联（ADMIN 拥有所有权限）
-- ============================================

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9);

-- ============================================
-- 完成
-- ============================================

SELECT '✅ 测试数据初始化完成！' AS message;
SELECT '测试账号：' AS info, username AS 用户名，'admin123' AS 密码 FROM sys_user WHERE deleted = 0;
