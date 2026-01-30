# Build mərhələsi
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test

# Run mərhələsi
FROM amazoncorretto:17-alpine
WORKDIR /app
# Gradle jar faylını build/libs qovluğuna yığır
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080 9091
ENTRYPOINT ["java", "-jar", "app.jar"]