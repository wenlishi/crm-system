# 🎯 CRM 客户管理系统

> 企业级 CRM 客户管理系统 - Java 学习实战项目

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📖 项目介绍

这是一个基于 SpringBoot 的企业级 CRM 客户管理系统，专为 Java 学习者设计。项目涵盖了企业级应用开发的核心技术栈，适合用于求职面试的作品集项目。

### 核心功能

- 👥 **客户管理** - 客户信息 CRUD、客户分级、公海池、导入导出 ✅
- 📞 **跟进管理** - 跟进记录、跟进提醒、跟进计划 ✅
- 📊 **商机管理** - 商机漏斗、阶段管理、成交预测 ✅
- 📝 **合同管理** - 合同创建、状态流转、合同统计 ✅
- 👤 **系统管理** - 用户管理、角色权限、数据字典 ✅
- 📈 **数据统计** - 销售报表、客户分析、数据可视化 ✅
- 🔐 **权限管理** - RBAC 权限控制、菜单权限、按钮权限 ✅
- 👥 **用户管理** - 用户 CRUD、密码管理、角色分配 ✅
- 📚 **数据字典** - 字典类型管理、下拉选项配置 ✅

### 技术栈

**后端核心**
- Java 17
- Spring Boot 3.2
- JWT 认证
- RBAC 权限管理
- MyBatis-Plus
- MySQL 8.0
- Redis 6.0

**中间件**
- RabbitMQ（异步通知）
- Elasticsearch（全文搜索）
- XXL-JOB（定时任务）

**部署运维**
- ✅ Docker（一键启动）
- ✅ Docker Compose
- Nginx
- Maven

---

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

**1. 克隆项目**
```bash
git clone https://github.com/wenlishi/crm-system.git
cd crm-system
```

**2. 创建数据库**
```bash
mysql -u root -p
CREATE DATABASE crm_system DEFAULT CHARACTER SET utf8mb4;
```

**3. 导入表结构**
```bash
mysql -u root -p crm_system < docs/sql/schema.sql
```

**4. 修改配置**
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/crm_system?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
```

**5. 启动项目**

**方式一：Maven 启动**
```bash
mvn spring-boot:run
```

**方式二：Docker 一键启动（推荐）** 🐳
```bash
docker-compose up -d
```

**6. 访问系统**
- 后端接口：http://localhost:8080/api
- Swagger 文档：http://localhost:8080/api/swagger-ui.html 📄
- 默认管理员账号：admin / admin123
- 用户管理：http://localhost:8080/api/users

**7. 查看 Docker 日志**
```bash
docker-compose logs -f crm-app
```

---

## 📁 项目结构

```
crm-system/
├── 📂 docs/                      # 文档目录
│   ├── 📄 需求文档.md
│   ├── 📄 数据库设计.md
│   ├── 📄 API 文档.md
│   └── 📂 sql/                   # SQL 脚本
│       ├── schema.sql            # 表结构
│       └── data.sql              # 初始化数据
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/com/crm/system/
│   │   │   ├── 📂 common/        # 公共模块
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── constant/     # 常量
│   │   │   │   ├── exception/    # 异常处理
│   │   │   │   └── utils/        # 工具类
│   │   │   ├── 📂 modules/       # 业务模块
│   │   │   │   ├── system/       # 系统管理
│   │   │   │   ├── customer/     # 客户管理
│   │   │   │   ├── follow/       # 跟进管理
│   │   │   │   ├── opportunity/  # 商机管理
│   │   │   │   └── contract/     # 合同管理
│   │   │   └── SystemApplication.java
│   │   └── 📂 resources/
│   │       ├── application.yml
│   │       └── 📂 mapper/
│   └── 📂 test/
├── 📄 pom.xml
└── 📄 README.md
```

---

## 🔧 核心功能演示

### 1. Redis 缓存客户数据

```java
@Service
public class CustomerService {
    
    @Cacheable(value = "customer", key = "#id")
    public Customer getById(Long id) {
        return customerMapper.selectById(id);
    }
}
```

### 2. 分布式锁防止重复跟进

```java
public void addFollowUp(FollowUp followUp) {
    String lockKey = "follow_lock:" + followUp.getCustomerId();
    // 使用 Redis 实现分布式锁
    if (redisLock.tryLock(lockKey)) {
        try {
            followUpMapper.insert(followUp);
        } finally {
            redisLock.unlock(lockKey);
        }
    }
}
```

### 3. RabbitMQ 异步邮件通知

```java
@RabbitListener(queues = "crm.email.queue")
public void sendEmail(FollowUp followUp) {
    // 异步发送邮件通知
    emailService.send(followUp);
}
```

---

## 📊 数据库设计

### 核心数据表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| crm_customer | 客户表 |
| crm_follow_up | 跟进记录表 |
| crm_opportunity | 商机表 |
| crm_contract | 合同表 |

详细设计文档：[docs/数据库设计.md](docs/数据库设计.md)

---

## 🎯 学习路线

### 第一阶段：基础搭建（1-2 周）
- [x] 项目初始化
- [ ] SpringBoot 配置
- [ ] 数据库连接
- [ ] 用户登录接口
- [ ] 客户管理 CRUD

### 第二阶段：功能完善（3-4 周）
- [ ] Redis 缓存集成
- [ ] 跟进管理模块
- [ ] 商机管理模块
- [ ] 合同管理模块
- [ ] 数据统计报表

### 第三阶段：进阶优化（5-6 周）
- [ ] RabbitMQ 集成
- [ ] Elasticsearch 搜索
- [ ] 定时任务
- [ ] 性能优化
- [ ] 压力测试

### 第四阶段：部署上线（7-8 周）
- [ ] Docker 容器化
- [ ] Nginx 配置
- [ ] 日志管理
- [ ] 监控告警
- [ ] 项目文档

---

## 📝 开发日志

| 日期 | 内容 | 状态 |
|------|------|------|
| 2026-03-14 | 项目设计、数据库设计 | ✅ |
| 2026-03-14 | 创建 GitHub 仓库 | ✅ |
| 2026-03-14 | SpringBoot 项目初始化 | ✅ |
| 2026-03-14 | 客户管理模块 | ✅ |
| 2026-03-14 | 跟进记录模块 | ✅ |
| 2026-03-14 | 数据统计模块 | ✅ |
| 2026-03-14 | 商机管理模块 | ✅ |
| 2026-03-14 | 合同管理模块 | ✅ |
| 2026-03-14 | 权限管理模块（RBAC） | ✅ |
| 2026-03-15 | 用户管理模块 | ✅ |
| 2026-03-15 | 数据字典模块 | ✅ |
| 2026-03-16 | 安全修复（密码加密/Token 解析） | ✅ |
| 2026-03-16 | Swagger 集成 | ✅ |
| 2026-03-16 | Docker 部署 | ✅ 🆕 |
| 待更新 | 前端开发、单元测试 | ⏳ |

---

## ❓ 常见问题

### Q: 这个项目适合谁？
A: 适合有一定 Java 基础，想系统学习企业级开发的开发者。也适合作为求职面试的作品集项目。

### Q: 需要多长时间完成？
A: 建议 2-3 个月，每周投入 10-15 小时。可以根据自己的节奏调整。

### Q: 遇到问题怎么办？
A: 欢迎提 Issue，我会尽快解答。也可以加入学习交流群讨论。

### Q: 可以商用吗？
A: 本项目采用 MIT 协议，可以免费用于学习和商用。

---

## 📚 学习资源

### 官方文档
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Redis](https://redis.io/documentation)

### 推荐书籍
- 《Spring Boot 实战》
- 《高性能 MySQL》
- 《Redis 设计与实现》

### 相关项目
- [RuoYi-Vue](https://github.com/yangzongzhuan/RuoYi-Vue)
- [mall](https://github.com/macrozheng/mall)

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源。

---

## 👨‍💻 作者

**wenlishi**
- GitHub: [@wenlishi](https://github.com/wenlishi)

---

## 🎉 致谢

感谢以下开源项目：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)

---

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！**

*Last Updated: 2026-03-16*  
*API Version: v1.9.0*  
*Total APIs: 70*  
*Swagger*: http://localhost:8080/api/swagger-ui.html 📄  
*Docker*: `docker-compose up -d` 🐳  
*GitHub: https://github.com/wenlishi/crm-system*
