# 📦 GitHub 仓库设置指南

**更新时间**：2026-03-14

---

## ✅ 本地 Git 已初始化完成

当前状态：
- ✅ Git 仓库已初始化
- ✅ 分支已重命名为 `main`
- ✅ 所有文件已添加
- ✅ 首次提交已完成

提交信息：
```
Initial commit: CRM 项目初始化

- ✨ 项目 README、LICENSE、.gitignore
- 📊 数据库初始化脚本（6 个核心表）
- 📖 快速开始指南文档
- 🏗️ 完整的项目目录结构

技术栈：SpringBoot 3.2 + MySQL 8.0 + Redis 6.0 + MyBatis-Plus
```

---

## 📋 下一步：创建 GitHub 仓库

### 步骤 1：访问 GitHub

打开浏览器，访问：https://github.com/new

### 步骤 2：填写仓库信息

| 字段 | 填写内容 |
|------|---------|
| **Repository name** | `crm-system` |
| **Description** | `企业级 CRM 客户管理系统 - Java 学习实战项目` |
| **Visibility** | ✅ Public（公开）或 🔒 Private（私有） |
| **Initialize with** | ❌ 不要勾选任何选项（我们已经初始化了） |

### 步骤 3：点击创建

点击 **"Create repository"** 按钮

---

## 🚀 推送代码到 GitHub

创建完仓库后，GitHub 会显示推送命令。执行以下命令：

```bash
# 1. 关联远程仓库
git remote add origin https://github.com/wenlishi/crm-system.git

# 2. 推送到 GitHub
git push -u origin main
```

### 如果提示需要认证

GitHub 现在使用 Personal Access Token 而不是密码。

**创建 Token 的步骤：**

1. 访问：https://github.com/settings/tokens
2. 点击 **"Generate new token (classic)"**
3. 填写说明（如：CRM Project）
4. 选择过期时间（建议 90 天）
5. 勾选权限：✅ `repo`（全部）
6. 点击 **"Generate token"**
7. **复制 Token**（只显示一次，务必保存好！）

**使用 Token 推送：**
```bash
# 推送时会提示输入用户名和密码
Username: YOUR_USERNAME
Password: 粘贴刚才复制的 Token（不会显示）
```

---

## ✅ 验证推送成功

推送完成后，刷新 GitHub 仓库页面，应该能看到：

- ✅ README.md 显示在页面顶部
- ✅ 项目文件结构
- ✅ 提交历史

---

## 📝 后续常用 Git 命令

### 查看状态
```bash
git status
```

### 查看提交历史
```bash
git log --oneline
```

### 添加新文件
```bash
git add .
git commit -m "添加新功能：客户管理模块"
git push
```

### 拉取最新代码
```bash
git pull origin main
```

### 创建新分支
```bash
# 创建并切换分支
git checkout -b feature/customer-module

# 推送分支到远程
git push -u origin feature/customer-module
```

---

## 🎯 项目链接

创建成功后，你的项目地址将是：

```
https://github.com/YOUR_USERNAME/crm-system
```

**记得在简历中放上这个链接！** 📎

---

## 📊 GitHub Profile 优化建议

### 1. 完善 README
- [x] 项目介绍（已完成）
- [x] 技术栈说明（已完成）
- [ ] 添加功能截图
- [ ] 添加部署演示地址
- [ ] 添加开发文档链接

### 2. 添加主题标签
在仓库设置中添加 Topics：
- `java`
- `spring-boot`
- `crm`
- `mysql`
- `redis`
- `learning-project`

### 3. 启用 GitHub Pages（可选）
如果有前端页面，可以启用 GitHub Pages 展示演示。

### 4. 添加 Issue 模板
创建 `.github/ISSUE_TEMPLATE` 目录，方便他人提交问题。

---

## 🔒 安全提醒

### ⚠️ 不要提交敏感信息

检查以下文件是否已添加到 `.gitignore`：
- ✅ `application-local.yml`（本地配置）
- ✅ `*.pem`、`*.key`（密钥文件）
- ✅ `.env`（环境变量）
- ✅ `target/`（编译产物）

### ✅ 使用环境变量

敏感配置使用环境变量：
```bash
# 在~/.bashrc 或~/.zshrc 中添加
export DB_PASSWORD=your_password
export REDIS_PASSWORD=your_redis_password
```

---

## 🎉 完成检查清单

- [ ] 创建 GitHub 仓库
- [ ] 执行 `git remote add origin`
- [ ] 执行 `git push -u origin main`
- [ ] 验证 GitHub 页面显示正常
- [ ] 添加 Topics 标签
- [ ] 复制项目链接到简历

---

**推送成功后，我们就可以继续完善项目代码了！** 🚀

*下一步：生成 SpringBoot 项目骨架代码*
