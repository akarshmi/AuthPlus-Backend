# =========================
# Stage 1: Build
# =========================
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy Maven wrapper & configs (better cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source
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


# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:25-jre-jammy

# Metadata
LABEL maintainer="authplus"
LABEL description="Spring Boot App (Java 25, Render optimized)"

# Install minimal runtime deps
RUN apt-get update && apt-get install -y --no-install-recommends \
    dumb-init \
    ca-certificates \
    tzdata \
    curl \
    fontconfig \
    fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# Timezone
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Non-root user
RUN groupadd -r spring -g 1001 && \
    useradd -r -g spring -u 1001 -m -s /usr/sbin/nologin spring

WORKDIR /app
RUN mkdir -p logs temp && chown -R spring:spring /app

# Copy JAR
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

# =========================
# Environment
# =========================
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# SAFE JVM settings for Render Free
ENV JAVA_OPTS="\
  -Xms128m \
  -Xmx384m \
  -XX:+UseG1GC \
  -XX:MaxRAMPercentage=70 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom \
  -Djava.awt.headless=true \
  -Dfile.encoding=UTF-8"

# Healthcheck (optional but recommended)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
