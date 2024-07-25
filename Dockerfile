# 使用官方的 OpenJDK 17 镜像作为基础镜像
FROM openjdk:17-jdk-alpine

# 将本地的 JAR 文件添加到容器中
COPY target/miaosha-0.0.1-SNAPSHOT.jar /app.jar

ENV SPRING_PROFILES_ACTIVE=prod

# 暴露容器端口
EXPOSE 8091

# 运行 Spring Boot 应用
CMD ["java", "-jar", "/app.jar"]