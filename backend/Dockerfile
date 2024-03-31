#FROM openjdk:21-jdk-oracle
#
#RUN mkdir -p /usr/app/
#
#ENV PROJECT_HOME /usr/app
#
#COPY build/libs/backend-0.0.1-SNAPSHOT-plain.jar $PROJECT_HOME/gooey-backend-0.0.1-SNAPSHOT.jar
#
#WORKDIR $PROJECT_HOME
#
#CMD ["java", "-cp", "./gooey-backend-0.0.1-SNAPSHOT.jar", "com.gooey.backend.BackendApplication.main"]

#FROM ubuntu:latest AS build
#
#RUN apt-get update
#RUN apt-get install openjdk-21-jdk -y
#COPY . .
#
#RUN ./gradlew bootJar --no-daemon
#
#FROM openjdk:21-jdk-slim
#
#EXPOSE 8080
#
#COPY --from=build /build/libs/backend-0.0.1-SNAPSHOT-plain.jar app.jar
#
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM gradle:8.5.0-jdk21
WORKDIR /app
COPY . /app

RUN gradle --no-daemon --warning-mode all --console=plain build

EXPOSE 8080
CMD ["sh", "start.sh"]