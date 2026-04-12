-- ============================================
-- CRM 系统 - 测试用户初始化脚本
-- ============================================

USE crm_system;

-- 插入测试用户（密码都是 admin123）
-- BCrypt hash for 'admin123': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi

INSERT INTO `sys_user` (`user_id`, `username`, `password`, `email`, `phone`, `status`, `deleted`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@crm.com', '13800138000', 1, 0),
(2, 'user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user1@crm.com', '13800138001', 1, 0),
(3, 'user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user2@crm.com', '13800138002', 1, 0);

SELECT '✅ 测试用户添加完成！' AS message;
SELECT username AS 用户名，email AS 邮箱，'admin123' AS 密码 FROM sys_user WHERE deleted = 0;
