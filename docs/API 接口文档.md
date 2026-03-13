# 📡 API 接口文档

**更新时间**：2026-03-14  
**版本**：v1.0.0

---

## 📋 接口列表

### 客户管理模块

#### 1. 查询客户列表

**接口**：`GET /api/customers`

**请求参数**：无

**返回示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "customerId": 123456789,
      "customerName": "张三",
      "customerType": 1,
      "level": 2,
      "phone": "13800138000",
      "email": "zhangsan@example.com",
      "status": 1,
      "createTime": "2026-03-14 01:00:00",
      "updateTime": "2026-03-14 01:00:00"
    }
  ],
  "timestamp": 1710378000000
}
```

---

#### 2. 分页查询客户

**接口**：`GET /api/customers/page`

**请求参数**：
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| current | Integer | 否 | 1 | 当前页码 |
| size | Integer | 否 | 10 | 每页数量 |

**请求示例**：
```
GET /api/customers/page?current=1&size=10
```

**返回示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": 1710378000000
}
```

---

#### 3. 根据 ID 查询客户

**接口**：`GET /api/customers/{id}`

**路径参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 客户 ID |

**请求示例**：
```
GET /api/customers/123456789
```

**返回示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "customerId": 123456789,
    "customerName": "张三",
    "customerType": 1,
    "level": 2,
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "status": 1
  },
  "timestamp": 1710378000000
}
```

---

#### 4. 新增客户

**接口**：`POST /api/customers`

**请求头**：
```
Content-Type: application/json
```

**请求体**：
```json
{
  "customerName": "李四",
  "customerType": 2,
  "level": 1,
  "phone": "13900139000",
  "email": "lisi@example.com",
  "companyName": "某某科技公司",
  "industry": "互联网",
  "source": "官网咨询",
  "status": 1
}
```

**返回示例**：
```json
{
  "code": 200,
  "message": "添加成功",
  "data": true,
  "timestamp": 1710378000000
}
```

---

#### 5. 更新客户

**接口**：`PUT /api/customers`

**请求头**：
```
Content-Type: application/json
```

**请求体**：
```json
{
  "customerId": 123456789,
  "customerName": "张三",
  "level": 3,
  "phone": "13800138000",
  "email": "zhangsan@example.com",
  "nextFollowTime": "2026-03-20 10:00:00"
}
```

**返回示例**：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": true,
  "timestamp": 1710378000000
}
```

---

#### 6. 删除客户

**接口**：`DELETE /api/customers/{id}`

**路径参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 客户 ID |

**请求示例**：
```
DELETE /api/customers/123456789
```

**返回示例**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": true,
  "timestamp": 1710378000000
}
```

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

### 时间格式

所有时间字段格式：`yyyy-MM-dd HH:mm:ss`

示例：`2026-03-14 01:40:00`

### 数据字典

**客户类型（customerType）**
- 1：个人
- 2：企业

**客户等级（level）**
- 1：普通
- 2：VIP
- 3：重要

**客户状态（status）**
- 0：失效
- 1：有效
- 2：已成交

---

## 🧪 测试方法

### 方式 1：使用 curl

```bash
# 查询客户列表
curl http://localhost:8080/api/customers

# 新增客户
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"customerName":"测试客户","phone":"13800138000"}'

# 删除客户
curl -X DELETE http://localhost:8080/api/customers/123456789
```

### 方式 2：使用 Postman

1. 下载 Postman：https://www.postman.com/downloads/
2. 创建请求，选择对应的方法和 URL
3. 设置请求头（POST/PUT 需要 Content-Type: application/json）
4. 填写请求体（JSON 格式）
5. 点击 Send 发送请求

### 方式 3：使用浏览器

直接访问 GET 接口：
```
http://localhost:8080/api/customers
http://localhost:8080/api/customers/123456789
```

---

## 📝 下一步

- [ ] 添加用户登录接口
- [ ] 添加权限控制
- [ ] 添加 Redis 缓存
- [ ] 添加跟进记录接口
- [ ] 添加商机管理接口
- [ ] 添加合同管理接口

---

*文档持续更新中...*
