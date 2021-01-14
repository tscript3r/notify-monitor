FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} notify.jar
ENTRYPOINT ["java","-jar","/notify.jar"]
VOLUME /logs
EXPOSE 8080 80 443 587