# Multi-stage build for Spring Boot
FROM maven:3.8-openjdk-17 AS build

# 작업 디렉토리 설정
WORKDIR /app

# pom.xml 먼저 복사 (캐시 최적화)
COPY pom.xml .

# 의존성 다운로드
RUN mvn dependency:go-offline -B

# 소스 코드 복사
COPY src ./src

# 애플리케이션 빌드
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/target/dashboard-backend-1.0.0.jar app.jar

# 포트 8000 노출
EXPOSE 8000

# 헬스체크 추가 (curl 설치)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8000/health || exit 1

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]