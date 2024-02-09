FROM openjdk:21.04-jdk-slim
RUN apt-get update && apt-get install bash
RUN mkdir -p /usr/app/
ENV PROJECT_HOME /usr/app/
COPY build/libs/gooey-backend-0.0.1-SNAPSHOT.jar $PROJECT_HOME/gooey-backend-0.0.1-SNAPSHOT.jar
WORKDIR $PROJECT_HOME
CMD ["java", "-jar", "./gooey-backend-0.0.1-SNAPSHOT.jar"]