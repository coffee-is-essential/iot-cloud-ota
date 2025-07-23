FROM gradle:8.14.3-jdk17-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle clean bootJar --no-daemon

FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
