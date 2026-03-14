#!/bin/bash

# ============================================
# CRM 权限管理模块测试脚本
# ============================================
# 用途：测试权限拦截器和 RBAC 权限控制
# 使用：./test-permission.sh
# ============================================

BASE_URL="http://localhost:8080/api"

echo "============================================"
echo "🔐 CRM 权限管理模块 API 测试"
echo "============================================"
echo ""

# 1. 登录获取 Token（管理员）
echo "📌 步骤 1: 管理员登录"
echo "-------------------------------------------"
ADMIN_TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
  echo "❌ 登录失败，请检查用户名密码或服务器状态"
  exit 1
fi
echo "✅ 管理员登录成功，Token: ${ADMIN_TOKEN:0:20}..."
echo ""

# 2. 查询权限树
echo "📌 步骤 2: 查询权限树"
echo "-------------------------------------------"
curl -s "$BASE_URL/permissions/tree" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data | length' | xargs -I {} echo "✅ 权限树节点数：{}"
echo ""

# 3. 查询所有可用权限
echo "📌 步骤 3: 查询所有可用权限"
echo "-------------------------------------------"
curl -s "$BASE_URL/permissions/list" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data | length' | xargs -I {} echo "✅ 可用权限数：{}"
echo ""

# 4. 查询所有角色
echo "📌 步骤 4: 查询所有角色"
echo "-------------------------------------------"
curl -s "$BASE_URL/roles/list" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data | length' | xargs -I {} echo "✅ 角色数：{}"
echo ""

# 5. 查询管理员的角色
echo "📌 步骤 5: 查询管理员（用户 ID=1）的角色"
echo "-------------------------------------------"
curl -s "$BASE_URL/roles/user/1" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data[].roleName'
echo ""

# 6. 查询管理员的权限
echo "📌 步骤 6: 查询管理员（用户 ID=1）的权限"
echo "-------------------------------------------"
curl -s "$BASE_URL/permissions/user/1" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data | length' | xargs -I {} echo "✅ 管理员权限数：{}"
echo ""

# 7. 测试客户列表接口（有权限）
echo "📌 步骤 7: 测试客户列表接口（管理员有权限）"
echo "-------------------------------------------"
curl -s "$BASE_URL/customers" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.code' | xargs -I {} echo "✅ 响应码：{} (期望 200)"
echo ""

# 8. 测试新增客户接口（有权限）
echo "📌 步骤 8: 测试新增客户接口（管理员有权限）"
echo "-------------------------------------------"
curl -s -X POST "$BASE_URL/customers" \
  -H "Content-Type: application/json" \
  -H "Authorization: $ADMIN_TOKEN" \
  -d '{
    "customerName": "测试客户",
    "customerType": 2,
    "level": 1,
    "phone": "13800138000"
  }' | jq '.code' | xargs -I {} echo "✅ 响应码：{} (期望 200)"
echo ""

# 9. 测试删除客户接口（有权限）
echo "📌 步骤 9: 测试删除客户接口（管理员有权限）"
echo "-------------------------------------------"
curl -s -X DELETE "$BASE_URL/customers/1" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.message' | xargs -I {} echo "✅ 响应：{}"
echo ""

# 10. 查询销售经理的权限
echo "📌 步骤 10: 查询销售经理（角色 ID=2）的权限"
echo "-------------------------------------------"
curl -s "$BASE_URL/permissions/role/2" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data | length' | xargs -I {} echo "✅ 销售经理权限数：{}"
echo ""

# 11. 查询销售专员的权限
echo "📌 步骤 11: 查询销售专员（角色 ID=3）的权限"
echo "-------------------------------------------"
curl -s "$BASE_URL/permissions/role/3" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data | length' | xargs -I {} echo "✅ 销售专员权限数：{}"
echo ""

# 12. 测试角色统计
echo "📌 步骤 12: 查询角色统计"
echo "-------------------------------------------"
curl -s "$BASE_URL/roles/stats" \
  -H "Authorization: $ADMIN_TOKEN" | jq '.data'
echo ""

# 13. 测试权限统计
echo "📌 步骤 13: 查询权限统计"
echo "-------------------------------------------"
curl -s "$BASE_URL/permissions/page?current=1&size=5" \
  -H "Authorization: $ADMIN_TOKEN" | jq '{total: .data.total, size: .data.size}'
echo ""

echo "============================================"
echo "✅ 权限管理模块测试完成！"
echo "============================================"
echo ""
echo "📝 测试说明："
echo "1. 管理员（super_admin）拥有所有 53 个权限"
echo "2. 销售经理（sales_manager）拥有 27 个权限"
echo "3. 销售专员（sales）拥有 21 个权限"
echo "4. 权限拦截器会自动校验 @RequirePermission 注解"
echo ""
echo "🔧 下一步："
echo "1. 创建测试用户并分配角色"
echo "2. 使用测试用户 Token 验证权限控制"
echo "3. 测试无权限访问返回 403"
