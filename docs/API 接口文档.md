# 📡 API 接口文档

**更新时间**：2026-03-14  
**版本**：v1.3.0

---

## 📋 接口列表

### 认证模块

| 接口 | 方法 | 说明 | 状态 |
|------|------|------|------|
| `/auth/login` | POST | 用户登录 | ✅ |
| `/auth/register` | POST | 用户注册 | ✅ |
| `/auth/info` | GET | 获取当前用户 | ✅ |

---

### 客户模块

| 接口 | 方法 | 说明 | 状态 |
|------|------|------|------|
| `/customers` | GET | 查询客户列表 | ✅ |
| `/customers/page` | GET | 分页查询 | ✅ |
| `/customers/{id}` | GET | 查询详情 | ✅ |
| `/customers` | POST | 新增客户 | ✅ |
| `/customers` | PUT | 更新客户 | ✅ |
| `/customers/{id}` | DELETE | 删除客户 | ✅ |

---

### 跟进记录模块

| 接口 | 方法 | 说明 | 状态 |
|------|------|------|------|
| `/follow-ups/customer/{id}` | GET | 查询客户跟进记录 | ✅ |
| `/follow-ups/page` | GET | 分页查询 | ✅ |
| `/follow-ups/{id}` | GET | 查询详情 | ✅ |
| `/follow-ups` | POST | 添加跟进 | ✅ |
| `/follow-ups` | PUT | 更新跟进 | ✅ |
| `/follow-ups/{id}` | DELETE | 删除跟进 | ✅ |

---

### 统计模块 🆕

#### 1. 首页统计数据

**接口**：`GET /api/statistics/dashboard`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCustomers": 1,
    "validCustomers": 1,
    "todayCustomers": 1,
    "totalFollowUps": 1,
    "weekFollowUps": 1
  },
  "timestamp": 1773428004467
}
```

**字段说明**：
- `totalCustomers`：客户总数
- `validCustomers`：有效客户数
- `todayCustomers`：今日新增客户
- `totalFollowUps`：跟进记录总数
- `weekFollowUps`：本周跟进次数

---

#### 2. 客户增长统计

**接口**：`GET /api/statistics/customer-growth`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "dailyStats": [
      {"date": "03-08", "count": 0},
      {"date": "03-09", "count": 0},
      {"date": "03-10", "count": 0},
      {"date": "03-11", "count": 0},
      {"date": "03-12", "count": 0},
      {"date": "03-13", "count": 0},
      {"date": "03-14", "count": 1}
    ]
  }
}
```

**说明**：近 7 天每天新增客户数，用于绘制增长趋势图。

---

#### 3. 跟进记录统计

**接口**：`GET /api/statistics/follow-up`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "byType": {
      "电话": 1,
      "微信": 0,
      "邮件": 0,
      "面谈": 0,
      "其他": 0
    },
    "trendStats": [
      {"date": "03-08", "count": 0},
      {"date": "03-09", "count": 0},
      {"date": "03-10", "count": 0},
      {"date": "03-11", "count": 0},
      {"date": "03-12", "count": 0},
      {"date": "03-13", "count": 0},
      {"date": "03-14", "count": 1}
    ]
  }
}
```

**字段说明**：
- `byType`：按跟进方式统计（电话/微信/邮件/面谈/其他）
- `trendStats`：近 7 天跟进趋势

---

## 🔧 通用说明

### 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 认证方式

所有需要登录的接口，需要在请求头中携带 Token：

```
Authorization: eyJhbGciOiJIUzI1NiJ9...
```

---

## 🧪 快速测试

### 使用 test-api.sh 脚本

```bash
cd /home/ubuntu/.openclaw/workspace/crm-system
./test-api.sh
```

### 使用 curl 命令

```bash
# 1. 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 2. 查询首页统计
curl http://localhost:8080/api/statistics/dashboard \
  -H "Authorization: $TOKEN"

# 3. 查询客户增长趋势
curl http://localhost:8080/api/statistics/customer-growth \
  -H "Authorization: $TOKEN"

# 4. 查询跟进统计
curl http://localhost:8080/api/statistics/follow-up \
  -H "Authorization: $TOKEN"
```

---

## 📊 接口统计

| 模块 | 接口数量 | 状态 |
|------|---------|------|
| 认证模块 | 3 | ✅ 完成 |
| 客户模块 | 6 | ✅ 完成 |
| 跟进记录模块 | 6 | ✅ 完成 |
| 统计模块 | 3 | ✅ 完成 |
| **总计** | **18** | **✅ 全部可用** |

---

## 🎯 技术亮点

### Redis 缓存

- ✅ 客户列表缓存（30 分钟）
- ✅ 客户详情缓存（30 分钟）
- ✅ 增删改自动清除缓存
- ✅ 响应时间 < 10ms

### 数据统计

- ✅ 实时统计数据
- ✅ 近 7 天趋势分析
- ✅ 多维度数据展示
- ✅ 支持前端图表绘制

---

*最后更新：2026-03-14*  
*版本：v1.3.0*
