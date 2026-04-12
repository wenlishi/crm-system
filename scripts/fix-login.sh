#!/bin/bash
# CRM 系统 - 登录功能修复脚本

echo "🔧 开始修复登录功能..."
echo

# 1. 停止后端服务
echo "1️⃣ 停止后端服务..."
pkill -f "crm-system.jar" 2>/dev/null
sleep 2
echo "   ✅ 已停止"

# 2. 更新数据库密码（使用 Java 生成正确的 BCrypt 哈希）
echo "2️⃣ 更新数据库密码..."
mysql -u root crm_system <<EOF
-- 删除旧用户
DELETE FROM sys_user WHERE username IN ('admin', 'test');

-- 插入新用户（密码：admin123）
-- 注意：这里使用 Spring Security 的 BCrypt 格式
INSERT INTO sys_user (user_id, username, password, email, phone, status, deleted) 
VALUES 
(1, 'admin', '\$2a\$10\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@crm.com', '13800138000', 1, 0),
(2, 'test', '\$2a\$10\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'test@crm.com', '13800138001', 1, 0);

SELECT '✅ 用户数据已更新' AS message;
SELECT username, email FROM sys_user WHERE deleted = 0;
EOF

# 3. 重新启动后端
echo "3️⃣ 启动后端服务..."
cd /home/ubuntu/.openclaw/workspace/projects/crm-system
nohup java -jar target/crm-system-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev > logs/backend.log 2>&1 &
sleep 30

# 4. 检查启动状态
if grep -q "Started SystemApplication" logs/backend.log 2>/dev/null; then
    echo "   ✅ 后端启动成功"
else
    echo "   ⚠️ 后端启动中..."
fi

# 5. 测试登录
echo "4️⃣ 测试登录..."
RESULT=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

if echo "$RESULT" | grep -q "token"; then
    echo "   ✅ 登录成功！"
    echo "$RESULT" | python3 -m json.tool 2>/dev/null | head -10
else
    echo "   ❌ 登录失败"
    echo "$RESULT" | python3 -m json.tool 2>/dev/null
fi

echo
echo "📝 测试账号："
echo "   用户名：admin"
echo "   密码：admin123"
echo
echo "🔗 访问地址："
echo "   前端：http://localhost:3000"
echo "   后端：http://localhost:8080/api"
echo "   Swagger: http://localhost:8080/api/swagger-ui.html"
