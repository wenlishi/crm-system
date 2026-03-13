#!/bin/bash

# CRM 系统 API 测试脚本
# 使用方法：./test-api.sh

BASE_URL="http://localhost:8080/api"
TOKEN=""

echo "======================================"
echo "🧪 CRM 系统 API 测试"
echo "======================================"
echo ""

# 测试 1：健康检查
echo "📌 测试 1：访问根路径"
curl -s $BASE_URL | head -1
echo ""
echo ""

# 测试 2：查询客户列表（需要登录）
echo "📌 测试 2：查询客户列表（未登录）"
curl -s $BASE_URL/customers
echo ""
echo ""

# 测试 3：用户登录
echo "📌 测试 3：用户登录"
LOGIN_RESULT=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')
echo $LOGIN_RESULT | python3 -m json.tool 2>/dev/null || echo $LOGIN_RESULT
echo ""

# 提取 Token
TOKEN=$(echo $LOGIN_RESULT | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
if [ -n "$TOKEN" ]; then
  echo "✅ 获取 Token 成功"
  echo ""
  
  # 测试 4：查询客户列表（已登录）
  echo "📌 测试 4：查询客户列表（已登录）"
  curl -s $BASE_URL/customers \
    -H "Authorization: $TOKEN"
  echo ""
  echo ""
  
  # 测试 5：新增客户
  echo "📌 测试 5：新增客户"
  curl -s -X POST $BASE_URL/customers \
    -H "Content-Type: application/json" \
    -H "Authorization: $TOKEN" \
    -d '{
      "customerName":"测试客户",
      "customerType":2,
      "level":1,
      "phone":"13800138000",
      "email":"test@example.com",
      "companyName":"测试公司",
      "industry":"互联网",
      "source":"官网",
      "status":1
    }'
  echo ""
  echo ""
else
  echo "⚠️  未获取到 Token，跳过后续测试"
fi

echo ""
echo "======================================"
echo "🎉 测试完成！"
echo "======================================"
