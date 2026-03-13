#!/bin/bash

# ============================================
# CRM 合同管理模块测试脚本
# ============================================
# 用途：测试合同管理相关 API 接口
# 使用：./test-contract.sh
# ============================================

BASE_URL="http://localhost:8080/api"

echo "============================================"
echo "📑 CRM 合同管理模块 API 测试"
echo "============================================"
echo ""

# 1. 登录获取 Token
echo "📌 步骤 1: 登录获取 Token"
echo "-------------------------------------------"
TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 登录失败，请检查用户名密码或服务器状态"
  exit 1
fi
echo "✅ 登录成功，Token: ${TOKEN:0:20}..."
echo ""

# 2. 查询合同统计
echo "📌 步骤 2: 查询合同统计"
echo "-------------------------------------------"
curl -s "$BASE_URL/contracts/stats" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 3. 新增合同
echo "📌 步骤 3: 新增合同"
echo "-------------------------------------------"
CONTRACT_RESPONSE=$(curl -s -X POST "$BASE_URL/contracts" \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "contractNo": "HT-2026-002",
    "contractName": "企业 CRM 系统采购合同",
    "customerId": 2032523096128479234,
    "opportunityId": 2032530000000000000,
    "amount": 50000.00,
    "signDate": "2026-03-14",
    "startDate": "2026-04-01",
    "endDate": "2027-03-31",
    "status": 1
  }')

echo "$CONTRACT_RESPONSE" | jq .

# 提取合同 ID
CONTRACT_ID=$(echo "$CONTRACT_RESPONSE" | jq -r '.data.contractId')
echo ""
echo "✅ 合同创建成功，ID: $CONTRACT_ID"
echo ""

# 4. 查询合同详情
echo "📌 步骤 4: 查询合同详情"
echo "-------------------------------------------"
curl -s "$BASE_URL/contracts/$CONTRACT_ID" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 5. 更新合同状态（审核通过）
echo "📌 步骤 5: 更新合同状态（审核通过）"
echo "-------------------------------------------"
curl -s -X PUT "$BASE_URL/contracts/$CONTRACT_ID/status?status=3" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 6. 更新合同状态（执行中）
echo "📌 步骤 6: 更新合同状态（执行中）"
echo "-------------------------------------------"
curl -s -X PUT "$BASE_URL/contracts/$CONTRACT_ID/status?status=4" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 7. 分页查询合同
echo "📌 步骤 7: 分页查询合同"
echo "-------------------------------------------"
curl -s "$BASE_URL/contracts/page?current=1&size=10" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 8. 查询客户的合同列表
echo "📌 步骤 8: 查询客户的合同列表"
echo "-------------------------------------------"
curl -s "$BASE_URL/contracts/customer/2032523096128479234" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 9. 再次查询合同统计（查看变化）
echo "📌 步骤 9: 再次查询合同统计（查看变化）"
echo "-------------------------------------------"
curl -s "$BASE_URL/contracts/stats" \
  -H "Authorization: $TOKEN" | jq .
echo ""

# 10. 删除合同
echo "📌 步骤 10: 删除合同"
echo "-------------------------------------------"
curl -s -X DELETE "$BASE_URL/contracts/$CONTRACT_ID" \
  -H "Authorization: $TOKEN" | jq .
echo ""

echo "============================================"
echo "✅ 合同管理模块测试完成！"
echo "============================================"
