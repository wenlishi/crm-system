#!/bin/bash

# ============================================
# CRM 系统 API 演示脚本
# ============================================
# 使用方法：
#   ./api-demo-script.sh
#
# 前提条件：
#   1. CRM 系统已启动（http://localhost:8080）
#   2. 已安装 jq（JSON 解析工具）
# ============================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# API 基础地址
BASE_URL="http://localhost:8080/api"
TOKEN=""

# 打印函数
print_header() {
    echo -e "\n${BLUE}============================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================${NC}\n"
}

print_step() {
    echo -e "${YELLOW}>>> $1${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}\n"
}

print_error() {
    echo -e "${RED}✗ $1${NC}\n"
}

# 检查系统是否可访问
check_system() {
    print_header "🔍 检查系统状态"
    
    response=$(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/swagger-ui.html)
    
    if [ "$response" == "200" ]; then
        print_success "CRM 系统运行正常"
    else
        print_error "CRM 系统未响应，请先启动系统"
        exit 1
    fi
}

# 1. 用户登录
login() {
    print_header "🔐 第一步：用户登录"
    print_step "请求：POST /api/auth/login"
    
    response=$(curl -s -X POST "$BASE_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d '{
            "username": "admin",
            "password": "admin123"
        }')
    
    echo "$response" | jq '.'
    
    # 提取 Token
    TOKEN=$(echo "$response" | jq -r '.data.token')
    
    if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
        print_success "登录成功！Token 已保存"
        export TOKEN
    else
        print_error "登录失败，请检查账号密码"
        exit 1
    fi
    
    echo ""
}

# 2. 获取用户信息
get_user_info() {
    print_header "👤 第二步：获取用户信息"
    print_step "请求：GET /api/auth/info"
    
    response=$(curl -s -X GET "$BASE_URL/auth/info" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$response" | jq '.'
    print_success "用户信息获取成功"
    echo ""
}

# 3. 创建客户
create_customer() {
    print_header "🏢 第三步：创建客户"
    print_step "请求：POST /api/customers"
    
    response=$(curl -s -X POST "$BASE_URL/customers" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "name": "演示科技公司",
            "contact": "王经理",
            "phone": "13800138000",
            "email": "wang@example.com",
            "source": "线上咨询",
            "level": "A",
            "address": "北京市海淀区中关村"
        }')
    
    echo "$response" | jq '.'
    
    # 提取客户 ID
    CUSTOMER_ID=$(echo "$response" | jq -r '.data.id')
    export CUSTOMER_ID
    
    if [ "$CUSTOMER_ID" != "null" ]; then
        print_success "客户创建成功！ID: $CUSTOMER_ID"
    fi
    echo ""
}

# 4. 查询客户列表
list_customers() {
    print_header "📋 第四步：查询客户列表"
    print_step "请求：GET /api/customers?page=1&size=10"
    
    response=$(curl -s -X GET "$BASE_URL/customers?page=1&size=10" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$response" | jq '.data.records[] | {id, name, contact, phone, level}'
    
    total=$(echo "$response" | jq '.data.total')
    print_success "共查询到 $total 个客户"
    echo ""
}

# 5. 查询客户详情
get_customer() {
    print_header "🔍 第五步：查询客户详情"
    print_step "请求：GET /api/customers/$CUSTOMER_ID"
    
    response=$(curl -s -X GET "$BASE_URL/customers/$CUSTOMER_ID" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$response" | jq '.'
    print_success "客户详情获取成功"
    echo ""
}

# 6. 更新客户
update_customer() {
    print_header "✏️ 第六步：更新客户信息"
    print_step "请求：PUT /api/customers/$CUSTOMER_ID"
    
    response=$(curl -s -X PUT "$BASE_URL/customers/$CUSTOMER_ID" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "name": "演示科技集团",
            "contact": "王经理",
            "phone": "13800138000",
            "level": "S",
            "status": "成交"
        }')
    
    echo "$response" | jq '.'
    print_success "客户信息更新成功"
    echo ""
}

# 7. 添加跟进记录
create_follow_up() {
    print_header "📞 第七步：添加跟进记录"
    print_step "请求：POST /api/follow-ups"
    
    response=$(curl -s -X POST "$BASE_URL/follow-ups" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $TOKEN" \
        -d '{
            "customerId": '"$CUSTOMER_ID"',
            "content": "电话沟通，客户对产品很感兴趣，约定下周演示产品",
            "type": "电话",
            "nextFollowUpTime": "2026-03-25 10:00:00"
        }')
    
    echo "$response" | jq '.'
    print_success "跟进记录添加成功"
    echo ""
}

# 8. 查询跟进历史
list_follow_ups() {
    print_header "📝 第八步：查询跟进历史"
    print_step "请求：GET /api/follow-ups?customerId=$CUSTOMER_ID"
    
    response=$(curl -s -X GET "$BASE_URL/follow-ups?customerId=$CUSTOMER_ID" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$response" | jq '.data[] | {id, content, type, createTime}'
    print_success "跟进历史查询成功"
    echo ""
}

# 9. 客户统计
customer_stats() {
    print_header "📊 第九步：客户统计"
    print_step "请求：GET /api/statistics/customers"
    
    response=$(curl -s -X GET "$BASE_URL/statistics/customers" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$response" | jq '.'
    print_success "统计数据获取成功"
    echo ""
}

# 10. 合同统计
contract_stats() {
    print_header "💰 第十步：合同统计"
    print_step "请求：GET /api/statistics/contracts"
    
    response=$(curl -s -X GET "$BASE_URL/statistics/contracts" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "$response" | jq '.'
    print_success "合同统计获取成功"
    echo ""
}

# 清理（删除测试数据）
cleanup() {
    print_header "🗑️ 清理测试数据"
    print_step "请求：DELETE /api/customers/$CUSTOMER_ID"
    
    if [ -n "$CUSTOMER_ID" ]; then
        curl -s -X DELETE "$BASE_URL/customers/$CUSTOMER_ID" \
            -H "Authorization: Bearer $TOKEN" | jq '.'
        print_success "测试数据已清理"
    fi
    echo ""
}

# 主函数
main() {
    print_header "🎬 CRM 系统 API 演示开始"
    echo "演示时间：$(date '+%Y-%m-%d %H:%M:%S')"
    echo "API 地址：$BASE_URL"
    echo ""
    
    # 检查系统
    check_system
    
    # 执行演示步骤
    login
    get_user_info
    create_customer
    list_customers
    get_customer
    update_customer
    create_follow_up
    list_follow_ups
    customer_stats
    contract_stats
    
    # 清理（可选）
    # cleanup
    
    print_header "✅ API 演示完成"
    echo "感谢观看！"
    echo ""
}

# 运行主函数
main
