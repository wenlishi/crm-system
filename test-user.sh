#!/bin/bash

# 用户管理模块测试脚本
# @author wenlishi
# @since 2026-03-15

BASE_URL="http://localhost:8080/api"

echo "======================================"
echo "🧪 用户管理模块测试脚本"
echo "======================================"
echo ""

# 1. 登录获取 Token
echo "📝 1. 登录获取 Token..."
TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ 登录失败，请检查服务是否启动"
    exit 1
fi
echo "✅ Token 获取成功：${TOKEN:0:20}..."
echo ""

# 2. 分页查询用户
echo "📝 2. 分页查询用户..."
curl -s "$BASE_URL/users/page?current=1&size=10" \
  -H "Authorization: $TOKEN" | jq '.'
echo ""

# 3. 查询用户列表（简单）
echo "📝 3. 查询所有用户..."
curl -s "$BASE_URL/users/list" \
  -H "Authorization: $TOKEN" | jq '.'
echo ""

# 4. 查询用户详情
echo "📝 4. 查询用户详情（ID=1）..."
curl -s "$BASE_URL/users/1" \
  -H "Authorization: $TOKEN" | jq '.'
echo ""

# 5. 新增用户
echo "📝 5. 新增用户..."
curl -s -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "username": "testuser001",
    "password": "123456",
    "email": "test@example.com",
    "phone": "13800138000",
    "deptId": 1,
    "status": 1
  }' | jq '.'
echo ""

# 6. 修改用户
echo "📝 6. 修改用户..."
# 先获取刚创建的用户 ID
USER_ID=$(curl -s "$BASE_URL/users/page?username=testuser001" \
  -H "Authorization: $TOKEN" | jq -r '.data.records[0].userId')

if [ "$USER_ID" != "null" ] && [ -n "$USER_ID" ]; then
    curl -s -X PUT "$BASE_URL/users" \
      -H "Content-Type: application/json" \
      -H "Authorization: $TOKEN" \
      -d "{
        \"userId\": $USER_ID,
        \"username\": \"testuser001\",
        \"email\": \"test_updated@example.com\",
        \"phone\": \"13900139000\",
        \"deptId\": 1,
        \"status\": 1
      }" | jq '.'
    echo ""
else
    echo "⚠️  未找到测试用户，跳过修改测试"
    echo ""
fi

# 7. 更新用户状态
echo "📝 7. 更新用户状态（禁用）..."
if [ "$USER_ID" != "null" ] && [ -n "$USER_ID" ]; then
    curl -s -X PUT "$BASE_URL/users/$USER_ID/status?status=0" \
      -H "Authorization: $TOKEN" | jq '.'
    echo ""
else
    echo "⚠️  未找到测试用户，跳过状态更新测试"
    echo ""
fi

# 8. 重置用户密码
echo "📝 8. 重置用户密码..."
if [ "$USER_ID" != "null" ] && [ -n "$USER_ID" ]; then
    curl -s -X PUT "$BASE_URL/users/$USER_ID/password/reset" \
      -H "Authorization: $TOKEN" | jq '.'
    echo ""
else
    echo "⚠️  未找到测试用户，跳过密码重置测试"
    echo ""
fi

# 9. 查询用户的角色
echo "📝 9. 查询用户的角色（ID=1）..."
curl -s "$BASE_URL/users/1/roles" \
  -H "Authorization: $TOKEN" | jq '.'
echo ""

# 10. 为用户分配角色
echo "📝 10. 为用户分配角色..."
curl -s -X POST "$BASE_URL/users/1/roles?roleIds=1,2" \
  -H "Authorization: $TOKEN" | jq '.'
echo ""

# 11. 删除用户
echo "📝 11. 删除用户..."
if [ "$USER_ID" != "null" ] && [ -n "$USER_ID" ]; then
    curl -s -X DELETE "$BASE_URL/users/$USER_ID" \
      -H "Authorization: $TOKEN" | jq '.'
    echo ""
else
    echo "⚠️  未找到测试用户，跳过删除测试"
    echo ""
fi

# 12. 批量删除用户
echo "📝 12. 批量删除用户..."
curl -s -X DELETE "$BASE_URL/users?ids=2,3" \
  -H "Authorization: $TOKEN" | jq '.'
echo ""

echo "======================================"
echo "✅ 用户管理模块测试完成！"
echo "======================================"
