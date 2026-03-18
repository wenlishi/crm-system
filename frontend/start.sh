#!/bin/bash

# CRM 系统前端启动脚本

echo "🚀 启动 CRM 前端开发服务器..."

cd "$(dirname "$0")"

# 检查 node_modules 是否存在
if [ ! -d "node_modules" ]; then
    echo "📦 首次运行，正在安装依赖..."
    npm install
fi

# 启动开发服务器
echo "✅ 启动成功！"
echo "🌐 访问地址：http://localhost:3000"
echo "📡 API 代理：http://localhost:8080"
echo ""
echo "默认账号：admin / admin123"
echo ""

npm run dev
