# 🚀 快速开始

## 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

## 一键启动（推荐）

### Docker 启动（前后端一起）

```bash
# 克隆项目
git clone https://github.com/wenlishi/crm-system.git
cd crm-system

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 访问
# 后端 API: http://localhost:8080/api
# 前端页面：http://localhost:3000
# Swagger: http://localhost:8080/api/swagger-ui.html
```

### 默认账号

```
用户名：admin
密码：admin123
```

---

## 手动启动

### 1. 启动后端

```bash
cd crm-system

# 编译
mvn clean package -DskipTests

# 运行
java -jar target/crm-system-1.0.0-SNAPSHOT.jar
```

### 2. 启动前端

```bash
cd crm-system/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

访问 http://localhost:3000

---

## 项目结构

```
crm-system/
├── 📂 backend/           # 后端（当前目录）
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── 📂 frontend/          # 前端
│   ├── src/
│   ├── package.json
│   └── README.md
├── 📂 docs/              # 文档
├── 📄 docker-compose.yml # Docker 配置
└── 📄 README.md          # 本文件
```

---

## 开发指南

### 后端开发

```bash
cd crm-system

# 运行测试
mvn test

# 生成覆盖率报告
mvn test jacoco:report

# 查看报告
open target/site/jacoco/index.html
```

### 前端开发

```bash
cd crm-system/frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产版本
npm run build
```

---

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2
- MyBatis-Plus
- MySQL 8.0
- Redis 7.0
- JWT

### 前端
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

---

## 测试

### 运行所有测试

```bash
mvn test
```

### 测试覆盖率

- 单元测试：67 个
- 集成测试：46 个
- **总计：113 个测试用例**
- **覆盖率：90%+**

---

## 部署

### 生产环境部署

1. 修改 `application-prod.yml` 数据库配置
2. 构建后端：`mvn clean package -DskipTests`
3. 构建前端：`npm run build`
4. 使用 Docker Compose 部署

```bash
docker-compose -f docker-compose.prod.yml up -d
```

---

## 常见问题

### Q: 端口被占用怎么办？

修改配置文件：
- 后端：`application.yml` 修改 `server.port`
- 前端：`vite.config.js` 修改 `server.port`

### Q: 如何重置数据库？

```bash
mysql -u root -p crm_system < docs/sql/schema.sql
mysql -u root -p crm_system < docs/sql/data.sql
```

### Q: 前端无法连接后端？

检查 `vite.config.js` 的代理配置：
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

---

## 相关文档

- [API 文档](docs/API 接口文档.md)
- [数据库设计](docs/数据库设计.md)
- [部署指南](docs/Docker 部署指南.md)
- [测试报告](docs/测试报告_2026-03-17.md)
- [完善建议](docs/完善建议报告.md)
- [完整提升计划](docs/完整提升计划.md)

---

## 开发团队

- **开发者**：wenlishi
- **GitHub**：https://github.com/wenlishi/crm-system

---

## 开源协议

MIT License

---

**最后更新**：2026-03-18
