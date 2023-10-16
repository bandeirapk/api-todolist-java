FROM ubuntu:22.04 AS build

RUN apt-get update
RUN apt-get install -y openjdk-17-jdk

COPY . .

RUN apt-get install -y maven
RUN mvn clean install

EXPOSE 8080

FROM openjdk:17-jdk-alpine

COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]