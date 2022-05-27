FROM openjdk:17-jdk-slim

RUN addgroup --system spring && adduser --ingroup spring --system spring
USER spring:spring

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
