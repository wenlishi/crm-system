# 🐳 Docker 部署指南

**版本**：v1.9.0  
**更新时间**：2026-03-16

---

## 📋 概述

使用 Docker Compose 一键部署 CRM 系统，包含：
- MySQL 8.0 数据库
- Redis 7 缓存
- CRM 应用（Spring Boot 3.2）

---

## 🚀 快速开始

### 1. 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 至少 2GB 可用内存

### 2. 一键启动

```bash
# 进入项目目录
cd /home/ubuntu/.openclaw/workspace/crm-system

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f crm-app
```

### 3. 访问系统

- **后端接口**：http://localhost:8080/api
- **Swagger 文档**：http://localhost:8080/api/swagger-ui.html
- **默认账号**：admin / admin123

---

## 📦 服务说明

| 服务 | 容器名 | 端口 | 说明 |
|------|--------|------|------|
| MySQL | crm-mysql | 3306 | 数据库 |
| Redis | crm-redis | 6379 | 缓存 |
| CRM 应用 | crm-app | 8080 | Spring Boot 应用 |

---

## 🔧 常用命令

### 启动/停止

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启所有服务
docker-compose restart

# 重启单个服务
docker-compose restart crm-app
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看应用日志
docker-compose logs -f crm-app

# 查看数据库日志
docker-compose logs -f mysql

# 查看最近 100 行
docker-compose logs --tail=100 crm-app
```

### 进入容器

```bash
# 进入应用容器
docker exec -it crm-app sh

# 进入数据库容器
docker exec -it crm-mysql mysql -uroot -proot123

# 进入 Redis 容器
docker exec -it crm-redis redis-cli
```

### 构建镜像

```bash
# 重新构建镜像
docker-compose build

# 强制重新构建（不使用缓存）
docker-compose build --no-cache

# 只构建应用镜像
docker build -t crm-system:latest .
```

---

## 📁 数据持久化

所有数据都保存在 `docker/` 目录：

```
docker/
├── mysql/
│   ├── data/          # MySQL 数据文件
│   └── conf/          # MySQL 配置文件
├── redis/
│   └── data/          # Redis 数据文件
└── app/
    ├── logs/          # 应用日志
    └── uploads/       # 上传文件
```

**清理数据**：
```bash
# 删除所有数据（谨慎！）
docker-compose down -v
rm -rf docker/
```

---

## ⚙️ 配置说明

### 环境变量

可以在 `docker-compose.yml` 中修改：

```yaml
environment:
  # 数据库配置
  - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/crm_system
  - SPRING_DATASOURCE_USERNAME=crm_user
  - SPRING_DATASOURCE_PASSWORD=crm123
  
  # Redis 配置
  - SPRING_REDIS_HOST=redis
  - SPRING_REDIS_PORT=6379
  
  # JVM 参数
  - JAVA_OPTS=-Xms512m -Xmx512m
```

### 端口映射

如需修改端口：

```yaml
ports:
  - "8080:8080"  # 改为其他端口，如 "8888:8080"
```

---

## 🔍 健康检查

### 检查服务状态

```bash
# 查看所有服务状态
docker-compose ps

# 查看详细信息
docker-compose top
```

### 健康检查端点

```bash
# 应用健康检查
curl http://localhost:8080/api/actuator/health

# Swagger 文档
curl http://localhost:8080/api/swagger-ui.html
```

---

## 🐛 常见问题

### 问题 1：容器启动失败

**解决**：
```bash
# 查看详细日志
docker-compose logs crm-app

# 检查端口占用
netstat -tlnp | grep 8080

# 重启服务
docker-compose restart
```

### 问题 2：数据库连接失败

**解决**：
```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 等待 MySQL 完全启动（首次启动需要 1-2 分钟）
```

### 问题 3：内存不足

**解决**：
```bash
# 修改 JVM 参数
environment:
  - JAVA_OPTS=-Xms256m -Xmx256m

# 重启应用
docker-compose restart crm-app
```

### 问题 4：数据丢失

**解决**：
```bash
# 确保数据持久化目录存在
ls -la docker/mysql/data
ls -la docker/redis/data

# 重新导入数据库
docker exec -i crm-mysql mysql -uroot -proot123 crm_system < docs/sql/schema.sql
```

---

## 📊 性能优化

### 1. 增加 JVM 内存

```yaml
environment:
  - JAVA_OPTS=-Xms1g -Xmx1g -XX:+UseG1GC
```

### 2. 使用 Docker Buildx 加速构建

```bash
# 启用 Buildx
docker buildx create --use

# 构建镜像
docker-compose build --build-arg BUILDKIT=1
```

### 3. 启用 Docker 缓存

```bash
# 使用缓存构建
docker-compose build --parallel
```

---

## 🎯 生产环境部署

### 1. 修改密码

```yaml
environment:
  MYSQL_ROOT_PASSWORD: 你的强密码
  MYSQL_PASSWORD: 你的强密码
```

### 2. 启用 HTTPS

使用 Nginx 反向代理：

```nginx
server {
    listen 443 ssl;
    server_name crm.example.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 3. 日志轮转

```yaml
logging:
  driver: "json-file"
  options:
    max-size: "100m"
    max-file: "3"
```

---

## 🎉 完成！

现在你的 CRM 系统可以：
- ✅ 一键启动（`docker-compose up -d`）
- ✅ 数据持久化
- ✅ 健康检查
- ✅ 日志管理
- ✅ 易于扩展

**下一步**：
- 📱 配置 Nginx 反向代理
- 🔐 配置 SSL 证书
- 📊 配置监控告警（Prometheus + Grafana）
- 🔄 配置 CI/CD（GitHub Actions）

---

*文档版本：v1.0*  
*最后更新：2026-03-16*
