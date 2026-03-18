# CRM 前端项目

基于 Vue 3 + Element Plus + Pinia + Vue Router 的 CRM 客户管理系统前端。

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **Vite** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Pinia** - Vue 状态管理
- **Vue Router** - 官方路由管理器
- **Axios** - HTTP 客户端
- **ECharts** - 数据可视化图表库

## 快速开始

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API 接口
│   │   ├── auth.js       # 认证接口
│   │   ├── customer.js   # 客户接口
│   │   ├── follow.js     # 跟进接口
│   │   ├── opportunity.js# 商机接口
│   │   ├── contract.js   # 合同接口
│   │   ├── user.js       # 用户接口
│   │   ├── role.js       # 角色接口
│   │   └── statistics.js # 统计接口
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   ├── layouts/          # 布局组件
│   │   └── MainLayout.vue
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── store/            # Pinia 状态管理
│   │   └── user.js
│   ├── utils/            # 工具函数
│   │   └── request.js
│   ├── views/            # 页面组件
│   │   ├── Login.vue
│   │   ├── Dashboard.vue
│   │   ├── customers/
│   │   ├── follow/
│   │   ├── opportunity/
│   │   ├── contract/
│   │   └── system/
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## 页面列表

| 页面 | 路由 | 说明 |
|------|------|------|
| 登录页 | `/login` | 用户登录 |
| 数据看板 | `/dashboard` | 数据统计和图表 |
| 客户管理 | `/customers` | 客户列表和详情 |
| 跟进记录 | `/follow-ups` | 跟进记录管理 |
| 商机管理 | `/opportunities` | 商机管理 |
| 合同管理 | `/contracts` | 合同管理 |
| 用户管理 | `/users` | 用户管理 |
| 角色权限 | `/roles` | 角色和权限管理 |

## 默认账号

```
用户名：admin
密码：admin123
```

## API 代理配置

开发环境下，所有 `/api` 请求会代理到后端服务器：

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

## 功能特性

- ✅ JWT 认证
- ✅ 路由守卫
- ✅ 请求/响应拦截器
- ✅ 统一错误处理
- ✅ 响应式布局
- ✅ 数据可视化图表
- ✅ CRUD 操作
- ✅ 分页查询
- ✅ 表单验证

## 开发规范

- 使用 Composition API (`<script setup>`)
- 组件名使用 PascalCase
- 使用 ESLint 进行代码检查

## 构建输出

生产构建后，文件输出到 `dist/` 目录：

```
dist/
├── index.html
├── static/
│   ├── css/
│   └── js/
└── ...
```

## 部署

将 `dist/` 目录部署到任意静态服务器即可。

推荐使用 Nginx：

```nginx
server {
    listen 80;
    server_name crm.example.com;
    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

---

**版本**: 1.0.0  
**创建时间**: 2026-03-18
