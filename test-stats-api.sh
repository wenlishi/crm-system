#!/bin/bash

# CRM 统计 API 测试脚本

BASE_URL="http://localhost:8080/api"
TOKEN=""

echo "======================================"
echo "CRM 统计 API 测试"
echo "======================================"
echo ""

# 第一步：登录获取 Token
echo "1. 登录获取 Token..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "登录响应："
echo "$LOGIN_RESPONSE" | jq .

# 提取 Token
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.token')

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo "❌ 登录失败，无法获取 Token"
  exit 1
fi

echo ""
echo "✅ Token 获取成功"
echo ""

# 设置认证头
AUTH_HEADER="Authorization: Bearer $TOKEN"

# 第二步：测试仪表盘统计
echo "======================================"
echo "2. 测试仪表盘统计接口..."
echo "======================================"
DASHBOARD_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics/dashboard" \
  -H "$AUTH_HEADER")

echo "响应："
echo "$DASHBOARD_RESPONSE" | jq .

# 检查字段
echo ""
echo "字段检查："
echo "$DASHBOARD_RESPONSE" | jq '.data | keys'

echo ""

# 第三步：测试客户增长统计
echo "======================================"
echo "3. 测试客户增长统计接口..."
echo "======================================"
GROWTH_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics/customer-growth" \
  -H "$AUTH_HEADER")

echo "响应："
echo "$GROWTH_RESPONSE" | jq .

# 检查数据结构
echo ""
echo "dailyStats 数据："
echo "$GROWTH_RESPONSE" | jq '.data.dailyStats'

echo ""

# 第四步：测试跟进统计
echo "======================================"
echo "4. 测试跟进统计接口..."
echo "======================================"
FOLLOWUP_RESPONSE=$(curl -s -X GET "$BASE_URL/statistics/follow-up" \
  -H "$AUTH_HEADER")

echo "响应："
echo "$FOLLOWUP_RESPONSE" | jq .

echo ""
echo "byType 数据："
echo "$FOLLOWUP_RESPONSE" | jq '.data.byType'

echo ""

# 第五步：测试合同统计
echo "======================================"
echo "5. 测试合同统计接口..."
echo "======================================"
CONTRACT_RESPONSE=$(curl -s -X GET "$BASE_URL/contracts/stats" \
  -H "$AUTH_HEADER")

echo "响应："
echo "$CONTRACT_RESPONSE" | jq .

echo ""

# 总结
echo "======================================"
echo "测试总结"
echo "======================================"
echo ""
echo "✅ 所有统计接口测试完成"
echo ""
echo "前端适配说明："
echo ""
echo "1. Dashboard 统计卡片："
echo "   - totalCustomers: $DASHBOARD_RESPONSE" | jq '.data.totalCustomers'
echo "   - totalFollowUps: $DASHBOARD_RESPONSE" | jq '.data.totalFollowUps'
echo ""
echo "2. 客户增长图表："
echo "   - 数据路径：data.dailyStats"
echo "   - 字段：date, count"
echo ""
echo "3. 跟进统计图表："
echo "   - 数据路径：data.trendStats"
echo "   - 字段：date, count"
echo ""
echo "4. 合同统计："
echo "   - 查看上方响应数据"
echo ""
