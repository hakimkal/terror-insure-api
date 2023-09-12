FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=web/target/*.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","app.jar"]