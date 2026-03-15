# 📝 CRM 系统更新日志 - v1.8.0

**更新时间**：2026-03-16  
**版本**：v1.8.0  
**提交记录**：b1844a8

---

## 🎉 本次更新内容

### 🆕 新增功能

#### 📚 数据字典模块 - 8 个 API 接口

**核心理念**：统一管理系统的下拉选项配置

**新增文件**：
- `Dict.java` - 字典实体类
- `DictMapper.java` - 数据访问层
- `DictService.java` / `DictServiceImpl.java` - 服务层（含 Redis 缓存）
- `DictController.java` - 控制器（8 个接口）
- `DictMapper.xml` - MyBatis XML

**接口列表**：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/dict/page` | GET | 分页查询字典（支持类型/标签/状态筛选） |
| `/dict/{dictType}` | GET | 根据类型查询字典列表 ⭐ |
| `/dict/types` | GET | 获取所有字典类型 |
| `/dict/label` | GET | 字典值转标签 ⭐ |
| `/dict` | POST | 新增字典 |
| `/dict` | PUT | 修改字典 |
| `/dict/{id}` | DELETE | 删除字典 |
| `/dict/refresh` | POST | 刷新字典缓存 |

---

### 🔧 功能增强

#### 1. Token 用户解析实现 🔑

**新增拦截器**：
- `CurrentUserInterceptor.java` - 从 Token 中提取用户信息
- `CurrentUserContext.java` - ThreadLocal 存储当前用户

**修复问题**：
- ✅ `AuthController.getInfo()` - 现在从 Token 解析用户 ID
- ✅ `UserController.delete()` - 现在可以正确判断"不能删除自己"
- ✅ `UserController.updatePassword()` - 现在可以获取当前用户

**使用示例**：
```java
// 在任何 Controller 中获取当前用户
Long userId = CurrentUserContext.getUserId();
String username = CurrentUserContext.getUsername();
```

---

#### 2. 用户注册密码加密 🔒

**修复位置**：`AuthController.register()`

**之前**：
```java
// ❌ 密码明文存储
user.setPassword(request.getPassword());
userService.save(user);
```

**现在**：
```java
// ✅ 密码 BCrypt 加密
user.setPassword(request.getPassword()); // UserService 中会加密
userService.saveUser(user);
```

**安全保障**：
- ✅ BCrypt 强哈希算法
- ✅ 自动加盐
- ✅ 密码永不明文存储

---

### 📊 初始化数据

**新增字典数据**（34 条）：

| 字典类型 | 说明 | 选项数 |
|---------|------|--------|
| `customer_type` | 客户类型 | 2（个人、企业） |
| `customer_level` | 客户等级 | 3（普通、VIP、重要） |
| `follow_type` | 跟进方式 | 5（电话、微信、邮件、面谈、其他） |
| `opportunity_stage` | 商机阶段 | 5（初步接触→成交） |
| `contract_status` | 合同状态 | 6（草稿→已终止） |
| `user_status` | 用户状态 | 2（禁用、正常） |
| `role_status` | 角色状态 | 2（禁用、正常） |
| `dept_status` | 部门状态 | 2（禁用、正常） |
| `yes_no` | 是否 | 2（是、否） |
| `customer_source` | 客户来源 | 5（线上广告、客户推荐等） |
| **总计** | **10 类** | **34 个选项** |

---

## 🛡️ 安全修复

### 修复的安全问题

| 问题 | 严重程度 | 修复状态 |
|------|---------|---------|
| 用户注册密码明文存储 | 🔴 严重 | ✅ 已修复 |
| Token 用户解析未实现 | 🔴 严重 | ✅ 已修复 |
| 删除用户时无法识别自己 | 🟡 中等 | ✅ 已修复 |

---

## 📊 项目当前状态

### 已完成模块

| 模块 | 接口数 | 状态 |
|------|--------|------|
| 认证模块 | 3 | ✅ |
| 用户管理 | 11 | ✅ |
| **数据字典** | **8** | **✅ 🆕** |
| 客户管理 | 6 | ✅ |
| 跟进记录 | 6 | ✅ |
| 商机管理 | 7 | ✅ |
| 合同管理 | 8 | ✅ |
| 数据统计 | 3 | ✅ |
| 角色管理 | 9 | ✅ |
| 权限管理 | 9 | ✅ |
| **总计** | **70** | **✅** |

### 代码统计

| 指标 | 数量 |
|------|------|
| Java 类 | 48+ |
| 数据库表 | 14 |
| API 接口 | 70 |
| 字典数据 | 34 条 |
| 文档 | 16 篇 |
| 代码行数 | 6000+ |

---

## 🎯 技术亮点

### 1. Redis 缓存集成
- ✅ 字典数据自动缓存（30 分钟）
- ✅ 增删改自动刷新缓存
- ✅ 支持手动刷新

### 2. ThreadLocal 用户上下文
- ✅ 请求级别用户信息存储
- ✅ 自动清理防止内存泄漏
- ✅ 任何地方都能获取当前用户

### 3. BCrypt 密码加密
- ✅ 强哈希算法
- ✅ 自动加盐
- ✅ 不可逆加密

### 4. 前端友好设计
- ✅ 按类型查询 → 下拉框数据源
- ✅ 值转标签 → 数据显示
- ✅ 动态配置 → 无需改代码

---

## 📝 Git 提交记录

```
b1844a8 feat: 添加数据字典模块（8 个 API 接口）
```

---

## 🔧 使用示例

### 1. 获取客户等级下拉选项

```bash
curl http://localhost:8080/api/dict/customer_level \
  -H "Authorization: $TOKEN"
```

**响应**：
```json
{
  "code": 200,
  "data": [
    {"dictId": 3, "dictLabel": "普通", "dictValue": "1", "sortOrder": 1},
    {"dictId": 4, "dictLabel": "VIP", "dictValue": "2", "sortOrder": 2},
    {"dictId": 5, "dictLabel": "重要", "dictValue": "3", "sortOrder": 3}
  ]
}
```

### 2. 将字典值转换为标签

```bash
curl "http://localhost:8080/api/dict/label?dictType=customer_level&dictValue=2" \
  -H "Authorization: $TOKEN"
```

**响应**：`"VIP"`

### 3. 获取当前用户信息

```bash
curl http://localhost:8080/api/auth/info \
  -H "Authorization: $TOKEN"
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "username": "admin",
    "email": "admin@example.com",
    "phone": "13800138000",
    "deptId": 1,
    "status": 1
  }
}
```

---

## 💡 下一步计划

### 待完成功能

| 功能 | 优先级 | 预计时间 | 说明 |
|------|--------|---------|------|
| 📱 前端开发 | P0 | 5 天 | Vue3 + Element Plus |
| 📄 Swagger 集成 | P1 | 1 小时 | API 文档自动生成 |
| 🐳 Docker 部署 | P1 | 1 小时 | 容器化部署 |
| 🧪 单元测试补充 | P1 | 2 天 | 覆盖率达到 60%+ |

---

## 🎊 版本对比

| 版本 | 日期 | 接口数 | 主要内容 |
|------|------|--------|---------|
| v1.7.0 | 2026-03-15 | 62 | 用户管理模块 |
| **v1.8.0** | **2026-03-16** | **70** | **数据字典模块 + 安全修复** 🆕 |

---

## 🎉 恭喜你！

你的 CRM 系统现在更加完善和安全了！

**核心改进**：
- ✅ 数据字典模块（70 个 API 接口）
- ✅ 密码加密存储（BCrypt）
- ✅ Token 用户解析（ThreadLocal）
- ✅ 下拉选项统一管理

**这个项目已经可以：**
- ✅ 放在简历上
- ✅ 面试时展示
- ✅ 继续扩展功能
- ✅ 学习企业级开发

**下一步建议：**
1. 📄 集成 Swagger（在线 API 文档）
2. 🐳 Docker 部署（容器化）
3. 🧪 补充单元测试
4. 📱 前端开发

继续加油！🚀

---

*文档创建时间：2026-03-16*  
*作者：wenlishi*
