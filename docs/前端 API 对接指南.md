# 🔌 前端 API 对接指南

> 更新时间：2026-03-18  
> 前端版本：v1.0.0  
> 后端版本：v1.8.0

---

## ✅ 已完成对接

### 1. 认证模块

| 前端方法 | 后端接口 | 状态 |
|---------|---------|------|
| `login()` | `POST /api/auth/login` | ✅ |
| `getCurrentUser()` | `GET /api/auth/info` | ✅ |
| `logout()` | `POST /api/auth/logout` | ✅ |

**响应格式**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGc...",
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "nickname": "管理员"
    }
  }
}
```

---

### 2. 客户模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getCustomerList(params)` | `GET /api/customers/page` | 分页查询 |
| `getCustomerDetail(id)` | `GET /api/customers/{id}` | 查询详情 |
| `createCustomer(data)` | `POST /api/customers` | 新增客户 |
| `updateCustomer(data)` | `PUT /api/customers` | 修改客户 |
| `deleteCustomer(id)` | `DELETE /api/customers/{id}` | 删除客户 |
| `getAllCustomers()` | `GET /api/customers?all=true` | 全部客户 |

**字段映射**：
```javascript
{
  customerId: '客户 ID',
  customerName: '客户名称',
  contact: '联系人',
  phone: '联系电话',
  email: '邮箱',
  level: '客户级别（A/B/C/D）',
  status: '状态（0 跟进中 1 已成交 2 已流失）',
  source: '来源（online/offline/referral/other）',
  ownerId: '负责人 ID',
  ownerName: '负责人姓名'
}
```

---

### 3. 跟进记录模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getFollowUpList(params)` | `GET /api/follow-ups/page` | 分页查询 |
| `getFollowUpDetail(id)` | `GET /api/follow-ups/{id}` | 查询详情 |
| `createFollowUp(data)` | `POST /api/follow-ups` | 新增跟进 |
| `updateFollowUp(data)` | `PUT /api/follow-ups` | 修改跟进 |
| `deleteFollowUp(id)` | `DELETE /api/follow-ups/{id}` | 删除跟进 |
| `getFollowUpByCustomerId(customerId)` | `GET /api/follow-ups/customer/{id}` | 按客户查询 |

**字段映射**：
```javascript
{
  followUpId: '跟进 ID',
  customerId: '客户 ID',
  title: '跟进标题',
  content: '跟进内容',
  contactType: '跟进方式（phone/wechat/email/visit）',
  nextContactTime: '下次联系时间',
  ownerId: '负责人 ID',
  ownerName: '负责人姓名'
}
```

---

### 4. 商机模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getOpportunityList(params)` | `GET /api/opportunities/page` | 分页查询 |
| `getOpportunityDetail(id)` | `GET /api/opportunities/{id}` | 查询详情 |
| `createOpportunity(data)` | `POST /api/opportunities` | 新增商机 |
| `updateOpportunity(data)` | `PUT /api/opportunities` | 修改商机 |
| `deleteOpportunity(id)` | `DELETE /api/opportunities/{id}` | 删除商机 |
| `getOpportunityStageStats()` | `GET /api/opportunities/stats/stage` | 阶段统计 |

**商机阶段**：
- `initial` - 初步接触
- `analysis` - 需求确认
- `proposal` - 方案报价
- `negotiation` - 谈判
- `won` - 成交
- `lost` - 输单

---

### 5. 合同模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getContractList(params)` | `GET /api/contracts/page` | 分页查询 |
| `getContractDetail(id)` | `GET /api/contracts/{id}` | 查询详情 |
| `createContract(data)` | `POST /api/contracts` | 新增合同 |
| `updateContract(data)` | `PUT /api/contracts` | 修改合同 |
| `deleteContract(id)` | `DELETE /api/contracts/{id}` | 删除合同 |
| `updateContractStatus(id, status)` | `PUT /api/contracts/{id}/status` | 更新状态 |
| `getContractByCustomerId(customerId)` | `GET /api/contracts/customer/{id}` | 按客户查询 |
| `getContractStats()` | `GET /api/contracts/stats` | 合同统计 |

**合同状态**：
- `draft` - 草稿
- `reviewing` - 待审核
- `active` - 已生效
- `completed` - 已完成
- `terminated` - 已终止

---

### 6. 用户模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getUserList(params)` | `GET /api/users/page` | 分页查询 |
| `getUserDetail(id)` | `GET /api/users/{id}` | 查询详情 |
| `createUser(data)` | `POST /api/users` | 新增用户 |
| `updateUser(data)` | `PUT /api/users` | 修改用户 |
| `deleteUser(id)` | `DELETE /api/users/{id}` | 删除用户 |
| `batchDeleteUsers(ids)` | `DELETE /api/users?ids=1,2,3` | 批量删除 |
| `updateUserStatus(id, status)` | `PUT /api/users/{id}/status` | 更新状态 |
| `resetUserPassword(id)` | `PUT /api/users/{id}/password/reset` | 重置密码 |
| `changePassword(old, new)` | `PUT /api/users/password` | 修改密码 |
| `assignUserRoles(userId, roleIds)` | `POST /api/users/{id}/roles` | 分配角色 |

---

### 7. 角色模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getRoleList(params)` | `GET /api/roles/page` | 分页查询 |
| `getRoleDetail(id)` | `GET /api/roles/{id}` | 查询详情 |
| `createRole(data)` | `POST /api/roles` | 新增角色 |
| `updateRole(data)` | `PUT /api/roles` | 修改角色 |
| `deleteRole(id)` | `DELETE /api/roles/{id}` | 删除角色 |
| `getAllRoles()` | `GET /api/roles?all=true` | 全部角色 |
| `getRolePermissions(roleId)` | `GET /api/roles/{id}/permissions` | 获取权限 |
| `assignRolePermissions(roleId, ids)` | `PUT /api/roles/{id}/permissions` | 分配权限 |

---

### 8. 统计模块

| 前端方法 | 后端接口 | 说明 |
|---------|---------|------|
| `getDashboardStats()` | `GET /api/statistics/dashboard` | 仪表盘统计 |
| `getCustomerGrowthStats(params)` | `GET /api/statistics/customer-growth` | 客户增长 |
| `getFollowUpStats()` | `GET /api/statistics/follow-up` | 跟进统计 |
| `getCustomerStats()` | `GET /api/statistics/customer` | 客户统计 |
| `getContractStats()` | `GET /api/contracts/stats` | 合同统计 |

**仪表盘响应示例**：
```json
{
  "code": 200,
  "data": {
    "customerCount": 156,
    "followUpCount": 320,
    "opportunityCount": 45,
    "contractAmount": 580000.00
  }
}
```

---

## 🔧 请求/响应拦截器

### 请求拦截器

```javascript
// src/utils/request.js
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})
```

### 响应拦截器

```javascript
request.interceptors.response.use(response => {
  const res = response.data
  
  if (res.code !== 200) {
    ElMessage.error(res.message || '请求失败')
    
    // 401: 未授权，需要重新登录
    if (res.code === 401) {
      ElMessageBox.confirm('登录已过期，请重新登录', '提示', {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        router.push('/login')
      })
    }
    
    return Promise.reject(new Error(res.message || '请求失败'))
  }
  
  return res
})
```

---

## 📝 使用示例

### 客户列表查询

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { getCustomerList } from '@/api/customer'

const tableData = ref([])
const pagination = ref({ current: 1, size: 10, total: 0 })

const loadData = async () => {
  const res = await getCustomerList({
    current: pagination.value.current,
    size: pagination.value.size,
    customerName: '测试',
    level: 'A'
  })
  
  tableData.value = res.data.records
  pagination.value.total = res.data.total
}

onMounted(() => {
  loadData()
})
</script>
```

### 新增客户

```vue
<script setup>
import { createCustomer } from '@/api/customer'

const handleSubmit = async () => {
  const formData = {
    customerName: '测试科技',
    contact: '张三',
    phone: '13800138000',
    email: 'test@example.com',
    level: 'A',
    source: 'online'
  }
  
  await createCustomer(formData)
  // 处理成功后的逻辑
}
</script>
```

### 图表数据加载

```vue
<script setup>
import { getDashboardStats, getCustomerGrowthStats } from '@/api/statistics'

const loadChart = async () => {
  // 加载仪表盘数据
  const stats = await getDashboardStats()
  console.log('客户总数:', stats.data.customerCount)
  
  // 加载客户增长数据
  const growth = await getCustomerGrowthStats({ months: 6 })
  console.log('增长数据:', growth.data)
}
</script>
```

---

## ⚠️ 注意事项

### 1. 日期格式

后端使用 `LocalDate` 和 `LocalDateTime`，前端传递日期时使用：
- 日期：`'YYYY-MM-DD'` 格式
- 日期时间：`'YYYY-MM-DD HH:mm:ss'` 格式

```javascript
// 使用 dayjs 格式化
import dayjs from 'dayjs'

const formData = {
  signDate: dayjs(date).format('YYYY-MM-DD'),
  createTime: dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}
```

### 2. 数字类型

金额等数字字段，后端使用 `BigDecimal`，前端直接使用 `Number` 即可。

```javascript
const formData = {
  amount: 50000.00,  // ✅ 直接传数字
  probability: 60    // ✅ 百分比
}
```

### 3. 枚举值

状态、级别等枚举值，前后端保持一致：

```javascript
// 客户级别
const level = 'A'  // A/B/C/D

// 合同状态
const status = 'active'  // draft/reviewing/active/completed/terminated

// 商机阶段
const stage = 'proposal'  // initial/analysis/proposal/negotiation/won/lost
```

### 4. 分页参数

后端使用 MyBatis-Plus 分页，参数名：
- `current` - 当前页码（从 1 开始）
- `size` - 每页数量

响应格式：
```json
{
  "records": [...],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

---

## 🐛 常见问题

### Q1: 401 未授权

**原因**：Token 过期或无效

**解决**：
1. 检查 localStorage 中是否有 token
2. 重新登录获取新 token
3. 检查后端 JWT 配置

### Q2: 403 权限不足

**原因**：用户没有该接口权限

**解决**：
1. 检查用户角色
2. 在角色管理中分配对应权限

### Q3: 跨域问题

**原因**：前后端端口不同

**解决**：
开发环境下已配置代理：
```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

### Q4: 日期格式错误

**原因**：前后端日期格式不一致

**解决**：
```javascript
// 前端统一使用字符串格式
const data = {
  date: '2026-03-18',
  dateTime: '2026-03-18 10:00:00'
}
```

---

## 📞 需要帮助？

如果遇到 API 对接问题：

1. **检查网络请求** - 打开浏览器 DevTools 查看 Network
2. **查看后端日志** - 确认后端是否收到请求
3. **检查响应格式** - 确认返回的 JSON 结构
4. **验证 Token** - 确认 Authorization header 是否正确

---

**API 对接完成！现在可以启动前后端进行测试了！** 🎉

*更新时间：2026-03-18 09:20*
