# =========================
# Stage 1: Build with Maven
# =========================
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy Maven wrapper & config first (better layer caching)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package \
    -DskipTests \
    -Dspring.profiles.active=prod \
    -Denforcer.skip=true \
    -Dcheckstyle.skip=true \
    -Dspotbugs.skip=true \
    -Djacoco.skip=true \
    -q


# ==============================
# Stage 2: Runtime (Java 25 JRE)
# ==============================
FROM eclipse-temurin:25-jre-jammy

# Metadata
LABEL maintainer="authplus.akarshmi.dev"
LABEL description="Spring Boot Application (Java 25)"
LABEL version="1.0.0"

# Install minimal OS dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      tzdata \
      curl \
      dumb-init \
      ca-certificates \
      fontconfig \
      fonts-dejavu-core \
      libfreetype6 \
    && rm -rf /var/lib/apt/lists/*

# Timezone
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create non-root user
RUN groupadd -r spring -g 1001 && \
    useradd -r -g spring -u 1001 -m -s /sbin/nologin spring

# App directories
WORKDIR /app
RUN mkdir -p logs temp config && \
    chown -R spring:spring /app

# Copy application JAR
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

# =====================
# Environment variables
# =====================
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# JVM options (Java 25 safe)
ENV JAVA_OPTS="\
  -Djava.security.egd=file:/dev/./urandom \
  -Djava.awt.headless=true \
  -Dfile.encoding=UTF-8 \
  -Djava.net.preferIPv4Stack=true \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseZGC \
  -XX:+ZGenerational \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/temp \
  -Xlog:gc*:file=/app/logs/gc.log:time,level,tags:filecount=5,filesize=10m"

# Healthcheck (Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

EXPOSE 8080

VOLUME ["/app/logs", "/app/temp", "/app/config"]

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
