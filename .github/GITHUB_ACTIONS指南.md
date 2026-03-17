# GitHub Actions 配置指南

> 配置时间：2026-03-17  
> 状态：✅ 已完成

---

## 📁 已创建的 Workflow 文件

### 1. CI 持续集成 (`ci.yml`)

**触发条件**：
- Push 到 `main/master/develop` 分支
- Pull Request

**执行步骤**：
1. ✅ 检出代码
2. ✅ 配置 JDK 17
3. ✅ Maven 编译
4. ✅ 运行测试
5. ✅ 生成 JaCoCo 覆盖率报告
6. ✅ 打包应用
7. ✅ 上传构建产物

**产出物**：
- JaCoCo 测试报告
- CRM 系统 JAR 包

---

### 2. Docker 构建推送 (`docker-build.yml`)

**触发条件**：
- Push 标签（如 `v1.0.0`）
- 手动触发

**执行步骤**：
1. ✅ 检出代码
2. ✅ 配置 JDK 17
3. ✅ Maven 打包
4. ✅ 登录 Docker Hub
5. ✅ 构建并推送 Docker 镜像

**产出物**：
- Docker 镜像（自动推送到 Docker Hub）

**需要配置 Secrets**：
- `DOCKER_USERNAME` - Docker Hub 用户名
- `DOCKER_PASSWORD` - Docker Hub 密码/Access Token

---

### 3. 代码质量检查 (`code-quality.yml`)

**触发条件**：
- Pull Request

**执行步骤**：
1. ✅ 检出代码
2. ✅ 配置 JDK 17
3. ✅ Maven 编译
4. ✅ 运行测试 + 覆盖率
5. ✅ 代码风格检查
6. ✅ 上传报告

**产出物**：
- JaCoCo 测试报告
- 测试结果报告

---

### 4. Release 发布 (`release.yml`)

**触发条件**：
- Push 标签（如 `v1.0.0`）

**执行步骤**：
1. ✅ 检出代码
2. ✅ 配置 JDK 17
3. ✅ Maven 打包
4. ✅ 创建 GitHub Release
5. ✅ 上传 JAR 包

**产出物**：
- GitHub Release（带构建产物）

---

## 🚀 使用指南

### 1. 推送到 GitHub

```bash
# 1. 初始化 Git（如果还没有）
cd /home/ubuntu/.openclaw/workspace/crm-system
git init
git add .
git commit -m "Initial commit"

# 2. 添加远程仓库
git remote add origin https://github.com/YOUR_USERNAME/crm-system.git

# 3. 推送代码
git push -u origin main
```

### 2. 查看 Actions 运行状态

1. 打开你的 GitHub 仓库
2. 点击 **Actions** 标签
3. 查看工作流运行状态

### 3. 触发不同工作流

#### CI 工作流
```bash
# 推送到分支自动触发
git push origin main
```

#### Docker 构建
```bash
# 打标签并推送
git tag v1.0.0
git push origin v1.0.0
```

#### Release 发布
```bash
# 打标签并推送（与 Docker 构建相同）
git tag v1.0.0
git push origin v1.0.0
```

---

## ⚙️ 配置 Secrets

### 在 GitHub 仓库中配置

1. 进入仓库 → **Settings**
2. 左侧菜单 → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**

### 需要配置的 Secrets

| Secret 名称 | 说明 | 示例 |
|-----------|------|------|
| `DOCKER_USERNAME` | Docker Hub 用户名 | `your-username` |
| `DOCKER_PASSWORD` | Docker Hub 密码或 Access Token | `xxx...` |

### 获取 Docker Access Token

1. 登录 https://hub.docker.com
2. 点击头像 → **Account Settings**
3. 左侧 → **Security**
4. 点击 **New Access Token**
5. 复制 Token 并保存（只显示一次）

---

## 📊 查看运行结果

### CI 工作流

**成功时**：
```
✅ Java CI
  ✅ Checkout code
  ✅ Set up JDK 17
  ✅ Build with Maven
  ✅ Run tests
  ✅ Generate JaCoCo report
  ✅ Package application
```

**失败时**：
- 点击失败的步骤查看详情
- 查看日志定位问题

---

## 📥 下载构建产物

### 方法 1：GitHub Actions 页面

1. 进入 **Actions** 标签
2. 点击具体的运行记录
3. 滚动到页面底部
4. 点击 **Artifacts** 下载

### 方法 2：GitHub Release

1. 进入 **Releases** 标签
2. 选择对应版本
3. 下载 Assets 中的 JAR 包

---

## 🔧 自定义配置

### 修改触发分支

编辑 `.github/workflows/ci.yml`：
```yaml
on:
  push:
    branches: [ main, develop, feature/* ]
```

### 添加通知

在 workflow 末尾添加：
```yaml
    - name: Notify on failure
      if: failure()
      run: |
        echo "Build failed!"
        # 可以添加邮件、Slack 等通知
```

### 添加缓存

Maven 缓存已自动配置：
```yaml
cache: maven
```

---

## 🐛 常见问题

### Q: Workflow 没有触发？
A: 检查：
1. 文件路径是否正确（`.github/workflows/`）
2. 分支名称是否匹配
3. YAML 语法是否正确

### Q: 测试失败怎么办？
A: 
1. 查看 Actions 日志
2. 本地运行 `mvn test` 复现
3. 修复后重新推送

### Q: 如何跳过 CI？
A: 在 commit 信息中添加：
```bash
git commit -m "feat: add new feature [skip ci]"
```

### Q: Docker 推送失败？
A: 检查：
1. Docker Hub 账号密码是否正确
2. 是否有推送权限
3. 镜像名称是否正确

---

## 📈 最佳实践

### 1. 分支策略
```
main        - 生产环境（受保护）
develop     - 开发分支
feature/*   - 功能分支
hotfix/*    - 热修复分支
```

### 2. 标签规范
```
v1.0.0      - 主版本.次版本.修订号
v1.0.0-beta - 测试版
v1.0.0-rc1  - 候选版
```

### 3. Commit 规范
```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

---

## ✅ 检查清单

在推送前确认：

- [ ] `.github/workflows/` 目录已创建
- [ ] 4 个 workflow 文件已创建
- [ ] 本地测试通过（`mvn test`）
- [ ] 代码已提交到 Git
- [ ] GitHub 仓库已创建
- [ ] Secrets 已配置（如需要 Docker）

---

## 🎯 下一步

1. **推送到 GitHub**
   ```bash
   git add .
   git commit -m "feat: add GitHub Actions CI/CD"
   git push origin main
   ```

2. **查看第一次运行**
   - 打开 GitHub 仓库
   - 点击 Actions 标签
   - 查看工作流运行

3. **配置 Docker Secrets**（可选）
   - 如果需要 Docker 镜像推送

4. **添加 Badge 到 README**
   ```markdown
   [![Java CI](https://github.com/YOUR_USERNAME/crm-system/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/crm-system/actions/workflows/ci.yml)
   ```

---

## 📝 文件清单

```
crm-system/
└── .github/
    └── workflows/
        ├── ci.yml              ✅ CI 持续集成
        ├── docker-build.yml    ✅ Docker 构建推送
        ├── code-quality.yml    ✅ 代码质量检查
        └── release.yml         ✅ Release 发布
```

---

*配置完成时间：2026-03-17 00:58*

**现在你的项目拥有完整的企业级 CI/CD 流水线了！** 🎉

---

## 🚀 立即使用

```bash
cd /home/ubuntu/.openclaw/workspace/crm-system

# 1. 初始化 Git（如果还没有）
git init
git add .
git commit -m "feat: initial commit with CI/CD"

# 2. 添加远程仓库（替换为你的仓库地址）
git remote add origin https://github.com/YOUR_USERNAME/crm-system.git

# 3. 推送代码
git push -u origin main

# 4. 打开 GitHub 查看 Actions 运行！
```

**推送后，GitHub Actions 会自动运行！** 🎊
