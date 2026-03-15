# 📡 API 接口文档

**更新时间**：2026-03-15  
**版本**：v1.8.0

---

## 📊 接口总览

| 模块 | 接口数 | 状态 |
|------|--------|------|
| 认证模块 | 3 | ✅ |
| **用户管理** | **11** | **✅** |
| **数据字典** | **8** | **✅ 🆕** |
| 客户模块 | 6 | ✅ |
| 跟进记录 | 6 | ✅ |
| 统计模块 | 3 | ✅ |
| 商机管理 | 7 | ✅ |
| 合同管理 | 8 | ✅ |
| 角色管理 | 9 | ✅ |
| 权限管理 | 9 | ✅ |
| **总计** | **70** | **✅** |

---

## 👤 用户管理模块 🆕

### 1. 分页查询用户

**接口**：`GET /api/users/page`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 否 | 用户名（模糊查询） |
| phone | String | 否 | 手机号（模糊查询） |
| deptId | Long | 否 | 部门 ID |
| status | Integer | 否 | 状态（0 禁用 1 正常） |
| current | Integer | 否 | 当前页码（默认 1） |
| size | Integer | 否 | 每页数量（默认 10） |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "userId": 1,
        "username": "admin",
        "email": "admin@example.com",
        "phone": "13800138000",
        "avatar": "https://example.com/avatar.png",
        "deptId": 1,
        "status": 1,
        "createTime": "2026-03-14T10:00:00",
        "updateTime": "2026-03-14T10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 2. 查询用户详情

**接口**：`GET /api/users/{id}`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 1,
    "username": "admin",
    "email": "admin@example.com",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar.png",
    "deptId": 1,
    "status": 1
  }
}
```

---

### 3. 新增用户

**接口**：`POST /api/users`

**请求**：
```json
{
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "avatar": "https://example.com/avatar.png",
  "deptId": 1,
  "status": 1
}
```

**字段约束**：
- `username`: 4-20 位字母、数字或下划线
- `password`: 6-20 位字母、数字或下划线
- `email`: 标准邮箱格式
- `phone`: 11 位手机号

---

### 4. 修改用户

**接口**：`PUT /api/users`

**请求**：
```json
{
  "userId": 2,
  "username": "zhangsan",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "avatar": "https://example.com/avatar.png",
  "deptId": 1,
  "status": 1
}
```

---

### 5. 删除用户

**接口**：`DELETE /api/users/{id}`

**响应**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": true
}
```

---

### 6. 批量删除用户

**接口**：`DELETE /api/users?ids=1,2,3`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | List<Long> | 是 | 用户 ID 列表 |

---

### 7. 更新用户状态

**接口**：`PUT /api/users/{id}/status?status=0`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 状态（0 禁用 1 正常） |

**用途**：禁用/启用用户

---

### 8. 重置用户密码

**接口**：`PUT /api/users/{id}/password/reset`

**说明**：将用户密码重置为默认密码 `123456`

---

### 9. 修改自己的密码

**接口**：`PUT /api/users/password?oldPassword=xxx&newPassword=xxx`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| oldPassword | String | 是 | 原密码 |
| newPassword | String | 是 | 新密码 |

---

### 10. 为用户分配角色

**接口**：`POST /api/users/{id}/roles?roleIds=1,2`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| roleIds | List<Long> | 是 | 角色 ID 列表 |

---

### 11. 查询用户的角色

**接口**：`GET /api/users/{id}/roles`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [1, 2]
}
```

---

## 📚 数据字典模块 🆕

### 1. 分页查询字典

**接口**：`GET /api/dict/page`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dictType | String | 否 | 字典类型（如：customer_level） |
| dictLabel | String | 否 | 字典标签（模糊查询） |
| status | Integer | 否 | 状态（0 禁用 1 正常） |
| current | Integer | 否 | 当前页码（默认 1） |
| size | Integer | 否 | 每页数量（默认 10） |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "dictId": 1,
        "dictType": "customer_level",
        "dictLabel": "普通",
        "dictValue": "1",
        "sortOrder": 1,
        "status": 1,
        "remark": "客户等级"
      }
    ],
    "total": 3,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

### 2. 根据字典类型查询字典列表

**接口**：`GET /api/dict/{dictType}`

**示例**：
```bash
curl http://localhost:8080/api/dict/customer_level \
  -H "Authorization: $TOKEN"
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {"dictId": 3, "dictType": "customer_level", "dictLabel": "普通", "dictValue": "1", "sortOrder": 1},
    {"dictId": 4, "dictType": "customer_level", "dictLabel": "VIP", "dictValue": "2", "sortOrder": 2},
    {"dictId": 5, "dictType": "customer_level", "dictLabel": "重要", "dictValue": "3", "sortOrder": 3}
  ]
}
```

**用途**：前端下拉框数据源

---

### 3. 获取所有字典类型

**接口**：`GET /api/dict/types`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {"dictType": "customer_level", "remark": "客户等级"},
    {"dictType": "customer_type", "remark": "客户类型"},
    {"dictType": "follow_type", "remark": "跟进方式"}
  ]
}
```

---

### 4. 根据字典类型和值查询标签

**接口**：`GET /api/dict/label?dictType=customer_level&dictValue=1`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "普通"
}
```

**用途**：将字典值转换为显示标签

---

### 5. 新增字典

**接口**：`POST /api/dict`

**请求**：
```json
{
  "dictType": "customer_level",
  "dictLabel": "超级 VIP",
  "dictValue": "4",
  "sortOrder": 4,
  "status": 1,
  "remark": "客户等级"
}
```

---

### 6. 修改字典

**接口**：`PUT /api/dict`

**请求**：
```json
{
  "dictId": 3,
  "dictType": "customer_level",
  "dictLabel": "普通客户",
  "dictValue": "1",
  "sortOrder": 1,
  "status": 1
}
```

---

### 7. 删除字典

**接口**：`DELETE /api/dict/{id}`

---

### 8. 刷新字典缓存

**接口**：`POST /api/dict/refresh`

**用途**：修改字典数据后刷新 Redis 缓存

---

## 🎯 商机管理模块

### 1. 查询客户的商机列表

**接口**：`GET /api/opportunities/customer/{customerId}`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "opportunityId": 2032530000000000000,
      "customerId": 2032523096128479234,
      "opportunityName": "企业 CRM 系统采购",
      "stage": 1,
      "expectedAmount": 50000,
      "probability": 60,
      "expectedCloseDate": "2026-04-15",
      "status": 1
    }
  ]
}
```

---

### 2. 分页查询商机

**接口**：`GET /api/opportunities/page`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| stage | Integer | 否 | 商机阶段（1-5） |
| current | Integer | 否 | 当前页码（默认 1） |
| size | Integer | 否 | 每页数量（默认 10） |

---

### 3. 添加商机

**接口**：`POST /api/opportunities`

**请求**：
```json
{
  "customerId": 2032523096128479234,
  "opportunityName": "企业 CRM 系统采购",
  "stage": 1,
  "expectedAmount": 50000,
  "probability": 60,
  "expectedCloseDate": "2026-04-15",
  "status": 1
}
```

**商机阶段**：
- 1：初步接触
- 2：需求确认
- 3：方案报价
- 4：谈判
- 5：成交

---

### 4. 获取阶段统计（销售漏斗）

**接口**：`GET /api/opportunities/stats/stage`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 1,
    "byStage": {
      "初步接触": 1,
      "需求确认": 0,
      "方案报价": 0,
      "谈判": 0,
      "成交": 0
    }
  }
}
```

**用途**：绘制销售漏斗图

---

## 📑 合同管理模块

### 1. 查询客户的合同列表

**接口**：`GET /api/contracts/customer/{customerId}`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "contractId": 2032540000000000000,
      "contractNo": "HT-2026-001",
      "contractName": "CRM 系统采购合同",
      "customerId": 2032523096128479234,
      "opportunityId": 2032530000000000000,
      "amount": 50000,
      "signDate": "2026-03-14",
      "startDate": "2026-04-01",
      "endDate": "2027-03-31",
      "status": 4,
      "fileUrl": "https://example.com/contract.pdf"
    }
  ]
}
```

---

### 2. 分页查询合同

**接口**：`GET /api/contracts/page`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 否 | 合同状态（1-6） |
| current | Integer | 否 | 当前页码（默认 1） |
| size | Integer | 否 | 每页数量（默认 10） |

---

### 3. 新增合同

**接口**：`POST /api/contracts`

**请求**：
```json
{
  "contractNo": "HT-2026-001",
  "contractName": "CRM 系统采购合同",
  "customerId": 2032523096128479234,
  "opportunityId": 2032530000000000000,
  "amount": 50000,
  "signDate": "2026-03-14",
  "startDate": "2026-04-01",
  "endDate": "2027-03-31",
  "status": 1
}
```

**合同状态**：
- 1：草稿
- 2：待审核
- 3：已审核
- 4：执行中
- 5：已完成
- 6：已终止

---

### 4. 获取合同统计

**接口**：`GET /api/contracts/stats`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "executing": 2,
    "completed": 2,
    "totalAmount": 250000.00
  }
}
```

**字段说明**：
- `total`：合同总数
- `executing`：执行中合同数
- `completed`：已完成合同数
- `totalAmount`：合同总金额

---

### 5. 更新合同状态

**接口**：`PUT /api/contracts/{id}/status`

**参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 新的合同状态 |

**示例**：
```bash
curl -X PUT "http://localhost:8080/api/contracts/123/status?status=4" \
  -H "Authorization: $TOKEN"
```

---

## 📋 完整接口列表

### 认证模块 (3 个)
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册
- `GET /auth/info` - 获取当前用户

### 客户模块 (6 个)
- `GET /customers` - 查询客户列表（Redis 缓存）
- `GET /customers/page` - 分页查询
- `GET /customers/{id}` - 查询详情
- `POST /customers` - 新增客户
- `PUT /customers` - 更新客户
- `DELETE /customers/{id}` - 删除客户

### 跟进记录 (6 个)
- `GET /follow-ups/customer/{id}` - 查询客户跟进记录
- `GET /follow-ups/page` - 分页查询
- `GET /follow-ups/{id}` - 查询详情
- `POST /follow-ups` - 添加跟进
- `PUT /follow-ups` - 更新跟进
- `DELETE /follow-ups/{id}` - 删除跟进

### 统计模块 (3 个)
- `GET /statistics/dashboard` - 首页统计数据
- `GET /statistics/customer-growth` - 客户增长趋势
- `GET /statistics/follow-up` - 跟进记录统计

### 商机管理 (7 个)
- `GET /opportunities/customer/{id}` - 查询客户商机
- `GET /opportunities/page` - 分页查询
- `GET /opportunities/{id}` - 查询详情
- `POST /opportunities` - 添加商机
- `PUT /opportunities` - 更新商机
- `DELETE /opportunities/{id}` - 删除商机
- `GET /opportunities/stats/stage` - 阶段统计

### 合同管理 (8 个) 🆕
- `GET /contracts/customer/{id}` - 查询客户合同
- `GET /contracts/page` - 分页查询
- `GET /contracts/{id}` - 查询详情
- `POST /contracts` - 新增合同
- `PUT /contracts` - 更新合同
- `DELETE /contracts/{id}` - 删除合同
- `GET /contracts/stats` - 合同统计
- `PUT /contracts/{id}/status` - 更新合同状态

---

## 🎯 技术亮点

### Redis 缓存
- ✅ 客户列表缓存（30 分钟）
- ✅ 客户详情缓存（30 分钟）
- ✅ 响应时间 < 10ms

### 销售漏斗
- ✅ 5 阶段商机管理
- ✅ 阶段统计接口
- ✅ 支持前端漏斗图绘制

### 数据统计
- ✅ 实时统计数据
- ✅ 近 7 天趋势分析
- ✅ 多维度数据展示

### 合同管理
- ✅ 合同全生命周期管理
- ✅ 合同状态流转（草稿→审核→执行→完成）
- ✅ 合同统计报表

---

## 🧪 快速测试

```bash
# 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 查询销售漏斗
curl http://localhost:8080/api/opportunities/stats/stage \
  -H "Authorization: $TOKEN"

# 查询合同统计
curl http://localhost:8080/api/contracts/stats \
  -H "Authorization: $TOKEN"

# 添加合同
curl -X POST http://localhost:8080/api/contracts \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "contractNo": "HT-2026-001",
    "contractName": "测试合同",
    "customerId": 123,
    "amount": 50000,
    "signDate": "2026-03-14",
    "status": 1
  }'

# 更新合同状态（审核通过）
curl -X PUT "http://localhost:8080/api/contracts/123/status?status=4" \
  -H "Authorization: $TOKEN"
```

---

*最后更新：2026-03-14*  
*版本：v1.5.0*  
*GitHub: https://github.com/wenlishi/crm-system*
