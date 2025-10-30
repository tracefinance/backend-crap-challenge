FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

RUN ls -la build/libs/

CMD ["java", "-jar", "build/libs/payment-api-0.0.1-all.jar"]

EXPOSE 8080
