FROM gradle:8.10-jdk17 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle :orders-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/src/orders-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
