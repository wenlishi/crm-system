# 📡 API 接口文档

**更新时间**：2026-03-14  
**版本**：v1.1.0

---

## 📋 接口列表

### 认证模块

#### 1. 用户登录

**接口**：`POST /api/auth/login`

**请求**：
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "expiration": 1774030718709
  },
  "timestamp": 1773425918709
}
```

---

#### 2. 用户注册

**接口**：`POST /api/auth/register`

**请求**：
```json
{
  "username": "newuser",
  "password": "password123"
}
```

---

### 客户模块

#### 1. 查询客户列表

**接口**：`GET /api/customers`

**请求头**：
```
Authorization: eyJhbGciOiJIUzI1NiJ9...
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "customerId": 2032523096128479234,
      "customerName": "测试客户",
      "customerType": 2,
      "level": 1,
      "phone": "13800138000",
      "email": "test@example.com",
      "status": 1,
      "createTime": "2026-03-14T02:24:03"
    }
  ]
}
```

---

#### 2. 分页查询客户

**接口**：`GET /api/customers/page`

**参数**：
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Integer | 否 | 1 | 当前页码 |
| size | Integer | 否 | 10 | 每页数量 |

---

#### 3. 根据 ID 查询客户

**接口**：`GET /api/customers/{id}`

---

#### 4. 新增客户

**接口**：`POST /api/customers`

**请求**：
```json
{
  "customerName": "测试客户",
  "customerType": 2,
  "level": 1,
  "phone": "13800138000",
  "email": "test@example.com",
  "status": 1
}
```

**响应**：
```json
{
  "code": 200,
  "message": "添加成功",
  "data": true
}
```

---

#### 5. 更新客户

**接口**：`PUT /api/customers`

---

#### 6. 删除客户

**接口**：`DELETE /api/customers/{id}`

---

### 跟进记录模块 🆕

#### 1. 查询客户的跟进记录

**接口**：`GET /api/follow-ups/customer/{customerId}`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "followId": 2032524292088127489,
      "customerId": 2032523096128479234,
      "userId": 1,
      "followType": 1,
      "content": "首次电话沟通，客户对产品很感兴趣",
      "nextPlan": "发送产品资料，安排演示",
      "createTime": "2026-03-14T02:28:48"
    }
  ]
}
```

---

#### 2. 分页查询跟进记录

**接口**：`GET /api/follow-ups/page`

**参数**：
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| customerId | Long | 是 | - | 客户 ID |
| current | Integer | 否 | 1 | 当前页码 |
| size | Integer | 否 | 10 | 每页数量 |

---

#### 3. 添加跟进记录

**接口**：`POST /api/follow-ups`

**请求**：
```json
{
  "customerId": 2032523096128479234,
  "userId": 1,
  "followType": 1,
  "content": "首次电话沟通，客户对产品很感兴趣",
  "nextPlan": "发送产品资料，安排演示"
}
```

**跟进方式**：
- 1：电话
- 2：微信
- 3：邮件
- 4：面谈
- 5：其他

---

#### 4. 更新跟进记录

**接口**：`PUT /api/follow-ups`

---

#### 5. 删除跟进记录

**接口**：`DELETE /api/follow-ups/{id}`

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

# 2. 查询客户列表
curl http://localhost:8080/api/customers \
  -H "Authorization: $TOKEN"

# 3. 添加跟进记录
curl -X POST http://localhost:8080/api/follow-ups \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "customerId": 2032523096128479234,
    "userId": 1,
    "followType": 1,
    "content": "电话沟通",
    "nextPlan": "安排演示"
  }'
```

---

## 📊 接口统计

| 模块 | 接口数量 | 状态 |
|------|---------|------|
| 认证模块 | 3 | ✅ 完成 |
| 客户模块 | 6 | ✅ 完成 |
| 跟进记录模块 | 6 | ✅ 完成 |
| **总计** | **15** | **✅ 全部可用** |

---

*最后更新：2026-03-14*  
*版本：v1.1.0*
