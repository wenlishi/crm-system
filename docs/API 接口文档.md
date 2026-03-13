# 📡 API 接口文档

**更新时间**：2026-03-14  
**版本**：v1.4.0

---

## 📊 接口总览

| 模块 | 接口数 | 状态 |
|------|--------|------|
| 认证模块 | 3 | ✅ |
| 客户模块 | 6 | ✅ |
| 跟进记录 | 6 | ✅ |
| 统计模块 | 3 | ✅ |
| 商机管理 | 7 | ✅ |
| **总计** | **25** | **✅** |

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

### 商机管理 (7 个) 🆕
- `GET /opportunities/customer/{id}` - 查询客户商机
- `GET /opportunities/page` - 分页查询
- `GET /opportunities/{id}` - 查询详情
- `POST /opportunities` - 添加商机
- `PUT /opportunities` - 更新商机
- `DELETE /opportunities/{id}` - 删除商机
- `GET /opportunities/stats/stage` - 阶段统计

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

# 添加商机
curl -X POST http://localhost:8080/api/opportunities \
  -H "Content-Type: application/json" \
  -H "Authorization: $TOKEN" \
  -d '{
    "customerId": 123,
    "opportunityName": "测试商机",
    "stage": 1,
    "expectedAmount": 10000,
    "probability": 50
  }'
```

---

*最后更新：2026-03-14*  
*版本：v1.4.0*  
*GitHub: https://github.com/wenlishi/crm-system*
