# 🎬 CRM 系统 API 演示录制脚本

> 录制时间：2026-03-18  
> 演示版本：v1.9.0  
> 录制工具：curl + Markdown

---

## 📋 演示大纲

### 第一部分：用户认证（2 分钟）
1. 用户登录
2. 获取 Token
3. Token 验证

### 第二部分：客户管理（5 分钟）
1. 创建客户
2. 查询客户列表
3. 更新客户信息
4. 删除客户

### 第三部分：跟进管理（3 分钟）
1. 添加跟进记录
2. 查询跟进历史

### 第四部分：数据统计（2 分钟）
1. 客户统计
2. 合同统计

---

## 🎥 演示脚本

### 1️⃣ 用户认证

#### 1.1 登录接口

```bash
# 请求
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "管理员",
      "roles": ["ADMIN"]
    }
  }
}
```

**演示要点**：
- ✅ 展示登录成功响应
- ✅ 复制 Token 用于后续请求
- ✅ 说明 Token 有效期 24 小时

---

#### 1.2 Token 验证

```bash
# 设置环境变量（方便后续使用）
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 验证 Token
curl -X GET http://localhost:8080/api/auth/info \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "roles": ["ADMIN"]
  }
}
```

**演示要点**：
- ✅ 展示如何携带 Token
- ✅ 验证用户信息

---

### 2️⃣ 客户管理

#### 2.1 创建客户

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "张三科技",
    "contact": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "source": "线上咨询",
    "level": "A",
    "address": "北京市朝阳区"
  }'

# 响应
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 101,
    "name": "张三科技",
    "contact": "张三",
    "phone": "13800138000",
    "createTime": "2026-03-18T00:00:00"
  }
}
```

**演示要点**：
- ✅ 展示完整的客户创建流程
- ✅ 说明必填字段
- ✅ 展示返回的客户 ID

---

#### 2.2 查询客户列表

```bash
# 分页查询
curl -X GET "http://localhost:8080/api/customers?page=1&size=10" \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "data": {
    "records": [
      {
        "id": 101,
        "name": "张三科技",
        "contact": "张三",
        "phone": "13800138000",
        "level": "A"
      },
      {
        "id": 102,
        "name": "李四贸易",
        "contact": "李四",
        "phone": "13900139000",
        "level": "B"
      }
    ],
    "total": 25,
    "page": 1,
    "size": 10
  }
}
```

**演示要点**：
- ✅ 展示分页功能
- ✅ 展示总记录数
- ✅ 说明默认每页 10 条

---

#### 2.3 查询客户详情

```bash
curl -X GET http://localhost:8080/api/customers/101 \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "data": {
    "id": 101,
    "name": "张三科技",
    "contact": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "source": "线上咨询",
    "level": "A",
    "address": "北京市朝阳区",
    "status": "跟进中",
    "ownerId": 1,
    "createTime": "2026-03-18T00:00:00"
  }
}
```

**演示要点**：
- ✅ 展示完整客户信息
- ✅ 说明客户状态流转

---

#### 2.4 更新客户信息

```bash
curl -X PUT http://localhost:8080/api/customers/101 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "张三科技集团",
    "contact": "张三",
    "phone": "13800138000",
    "level": "S",
    "status": "成交"
  }'

# 响应
{
  "code": 200,
  "message": "更新成功",
  "data": {
    "id": 101,
    "name": "张三科技集团",
    "level": "S",
    "status": "成交"
  }
}
```

**演示要点**：
- ✅ 展示部分更新
- ✅ 说明客户升级流程

---

#### 2.5 删除客户

```bash
curl -X DELETE http://localhost:8080/api/customers/101 \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "message": "删除成功"
}
```

**演示要点**：
- ✅ 展示软删除机制
- ✅ 说明数据可恢复

---

### 3️⃣ 跟进管理

#### 3.1 添加跟进记录

```bash
curl -X POST http://localhost:8080/api/follow-ups \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "customerId": 102,
    "content": "电话沟通，客户对产品很感兴趣，约定下周演示",
    "type": "电话",
    "nextFollowUpTime": "2026-03-25 10:00:00"
  }'

# 响应
{
  "code": 200,
  "message": "跟进成功",
  "data": {
    "id": 501,
    "customerId": 102,
    "content": "电话沟通，客户对产品很感兴趣，约定下周演示",
    "type": "电话",
    "createTime": "2026-03-18T00:00:00"
  }
}
```

**演示要点**：
- ✅ 展示跟进记录创建
- ✅ 说明下次跟进提醒

---

#### 3.2 查询跟进历史

```bash
curl -X GET "http://localhost:8080/api/follow-ups?customerId=102" \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "data": [
    {
      "id": 501,
      "content": "电话沟通，客户对产品很感兴趣",
      "type": "电话",
      "creatorName": "管理员",
      "createTime": "2026-03-18T00:00:00"
    },
    {
      "id": 498,
      "content": "初次拜访，了解客户需求",
      "type": "拜访",
      "creatorName": "销售员 A",
      "createTime": "2026-03-15T00:00:00"
    }
  ]
}
```

**演示要点**：
- ✅ 展示跟进时间线
- ✅ 展示跟进人信息

---

### 4️⃣ 数据统计

#### 4.1 客户统计

```bash
curl -X GET http://localhost:8080/api/statistics/customers \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "data": {
    "total": 156,
    "thisWeek": 12,
    "thisMonth": 45,
    "byLevel": {
      "S": 5,
      "A": 28,
      "B": 67,
      "C": 56
    },
    "byStatus": {
      "跟进中": 89,
      "意向": 34,
      "成交": 23,
      "流失": 10
    }
  }
}
```

**演示要点**：
- ✅ 展示数据看板
- ✅ 说明客户分级分布

---

#### 4.2 合同统计

```bash
curl -X GET http://localhost:8080/api/statistics/contracts \
  -H "Authorization: Bearer $TOKEN"

# 响应
{
  "code": 200,
  "data": {
    "totalAmount": 1250000.00,
    "thisMonth": 380000.00,
    "byStatus": {
      "草稿": 5,
      "审核中": 3,
      "执行中": 12,
      "已完成": 8
    }
  }
}
```

**演示要点**：
- ✅ 展示销售金额
- ✅ 说明合同状态分布

---

## 🎬 录制方式

### 方式一：终端录制（推荐）

使用 `asciinema` 录制终端操作：

```bash
# 安装 asciinema
sudo apt install asciinema  # Linux
brew install asciinema      # Mac

# 开始录制
asciinema rec api-demo.cast

# 执行上面的 curl 命令...

# 结束录制（Ctrl+D）
# 生成可分享的链接
asciinema upload api-demo.cast
```

---

### 方式二：屏幕录制

**工具推荐**：
- OBS Studio（免费开源）
- QuickTime Player（Mac 自带）
- 腾讯会议（录制屏幕）

**录制设置**：
- 分辨率：1920x1080
- 帧率：30fps
- 格式：MP4

---

### 方式三：Swagger UI 演示

1. 访问：http://localhost:8080/api/swagger-ui.html
2. 点击 Authorizations 添加 Token
3. 逐个接口点击 Try it out
4. 使用录屏软件录制

---

## 📝 演示文案

### 开场白（30 秒）

> 大家好，今天给大家演示我们的 CRM 客户管理系统 API。
> 
> 这个系统基于 SpringBoot 3.2 开发，包含客户管理、跟进管理、商机管理、合同管理等核心功能。
> 
> 现在我们开始演示...

---

### 结束语（30 秒）

> 以上就是 CRM 系统的主要 API 演示。
> 
> **项目亮点**：
> - 完整的 RESTful API 设计
> - JWT 安全认证
> - RBAC 权限控制
> - 85% 测试覆盖率
> - Docker 一键部署
> 
> 感谢观看！

---

## 📊 演示检查清单

### 演示前准备
- [ ] 启动 CRM 系统
- [ ] 准备测试数据
- [ ] 测试所有 API 可用
- [ ] 准备 Token
- [ ] 清理屏幕桌面

### 演示中注意
- [ ] 语速适中
- [ ] 关键信息停顿
- [ ] 鼠标移动平稳
- [ ] 避免误操作

### 演示后处理
- [ ] 剪辑视频
- [ ] 添加字幕
- [ ] 上传到平台
- [ ] 分享链接

---

## 🔗 分享平台

- **B 站**：https://www.bilibili.com/
- **YouTube**：https://www.youtube.com/
- **GitHub**：嵌入 README
- **语雀**：技术文档

---

## 📁 相关文件

- `api-demo-script.sh` - 自动化演示脚本
- `api-demo-responses.json` - 示例响应
- `api-demo-video.mp4` - 录制视频

---

*演示脚本版本：v1.0*  
*最后更新：2026-03-18*
