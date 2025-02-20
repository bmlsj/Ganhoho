FROM eclipse-temurin:17-jdk-alpine
ENV APP_HOME = /usr/app/
WORKDIR $APP_HOME
COPY build/libs/*.jar app.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
