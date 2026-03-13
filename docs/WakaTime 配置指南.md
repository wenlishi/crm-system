# ⏱️ WakaTime 配置指南

**配置时间**：2026-03-14  
**项目**：CRM 客户管理系统  
**状态**：✅ 已配置

---

## 📋 什么是 WakaTime？

WakaTime 是一个自动化的编程时间追踪工具，可以：
- 📊 自动记录你在每个项目上的编码时间
- 📈 生成详细的编程时间统计报表
- 🎯 追踪你使用的编程语言和技术栈
- 💡 帮助你了解时间分配，提高效率

---

## 🔧 配置步骤

### 1. 注册 WakaTime 账号

访问：https://wakatime.com/signup

使用 GitHub 账号快速登录即可。

### 2. 获取 API Key

登录后访问：https://wakatime.com/api-key

复制你的 API Key（格式类似：`xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`）

### 3. 配置 API Key

编辑配置文件：
```bash
nano ~/.wakatime.cfg
```

将 `YOUR_WAKATIME_API_KEY` 替换为你的真实 API Key：
```ini
[settings]
api_key = xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

### 4. 验证配置

测试 WakaTime 是否工作：
```bash
wakatime --file /home/ubuntu/.openclaw/workspace/crm-system/src/main/java/com/crm/system/SystemApplication.java --entity /home/ubuntu/.openclaw/workspace/crm-system/src/main/java/com/crm/system/SystemApplication.java
```

---

## 🎯 IDE 集成（推荐）

### IntelliJ IDEA / Android Studio

1. 打开 `Settings` → `Plugins`
2. 搜索 `WakaTime`
3. 点击 `Install` 安装
4. 重启 IDE
5. 首次启动会提示输入 API Key

### VS Code

1. 打开扩展面板（Ctrl+Shift+X）
2. 搜索 `WakaTime`
3. 点击安装
4. 首次使用会提示输入 API Key

---

## 📊 查看统计

访问你的 Dashboard：https://wakatime.com/dashboard

可以看到：
- 📈 今日/本周/本月的编程时间
- 📊 项目时间分配
- 💻 使用的语言和编辑器
- 📅 编程习惯分析

---

## 🎯 CRM 项目配置

### 项目目录
```
/home/ubuntu/.openclaw/workspace/crm-system/
```

### 追踪的文件类型
- ✅ Java (.java)
- ✅ XML (.xml)
- ✅ YAML (.yml, .yaml)
- ✅ SQL (.sql)
- ✅ Markdown (.md)
- ✅ Shell (.sh)

### 排除的目录
- ❌ target/ (编译产物)
- ❌ .git/ (版本控制)
- ❌ node_modules/ (依赖包)
- ❌ .idea/ (IDE 配置)
- ❌ .vscode/ (编辑器配置)

---

## 📈 使用建议

### 1. 自动追踪
WakaTime 会自动追踪你的编码活动，无需手动操作。

### 2. 查看周报
每周一查看上周的编程时间统计，了解自己的学习进度。

### 3. 设定目标
例如：
- 每天编程 2 小时
- 每周编程 15 小时
- 每月完成 1 个功能模块

### 4. 分享成果
可以将统计图表分享到：
- 简历中展示学习投入
- 社交媒体分享学习进度
- 面试时展示项目投入时间

---

## 🎊 示例统计

### 一周编程时间
```
总编程时间：25 小时 30 分钟
日均编程时间：3 小时 40 分钟

项目分布：
- CRM 系统：18 小时 (70%)
- 学习文档：5 小时 (20%)
- 其他：2.5 小时 (10%)

语言分布：
- Java：20 小时 (78%)
- XML：3 小时 (12%)
- Markdown：2 小时 (8%)
- 其他：0.5 小时 (2%)
```

---

## 🔗 相关链接

- 官网：https://wakatime.com
- API 文档：https://wakatime.com/developers
- 统计 Dashboard：https://wakatime.com/dashboard
- 团队版：https://wakatime.com/teams

---

## 💡 小贴士

1. **保护隐私**：可以设置隐藏文件名和项目名
2. **离线工作**：WakaTime 会缓存数据，联网后自动同步
3. **多设备同步**：所有设备的编码时间会自动合并
4. **导出数据**：可以导出 CSV 格式的时间数据

---

*配置时间：2026-03-14*  
*项目：CRM 客户管理系统*
