FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# JAR 파일 복사 (로컬에서 빌드된 JAR 파일)
COPY build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]