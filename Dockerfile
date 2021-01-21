FROM openjdk:8-jdk-alpine
RUN apk add --no-cache tzdata
ENV TZ Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} notify.jar
ENTRYPOINT ["java","-jar","/notify.jar"]
VOLUME /logs
EXPOSE 8888