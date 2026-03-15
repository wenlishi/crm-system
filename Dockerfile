# 多阶段构建 - 构建阶段
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml（利用 Docker 缓存）
COPY pom.xml .

# 下载依赖（如果 pom.xml 没变化，使用缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 打包构建（跳过测试）
RUN mvn clean package -DskipTests -B

# ============================================

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 安装必要工具
RUN apk add --no-cache curl

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
