FROM openjdk:21-jdk-oracle
RUN apt-get update && apt-get install bash
RUN mkdir -p /usr/app/
ENV PROJECT_HOME /usr/app
COPY build/libs/gooey-backend-0.0.1-SNAPSHOT.ojar $PROJECT_HOME/gooey-backend-0.0.1-SNAPSHOT.jar
WORKDIR $PROJECT_HOME
CMD ["java", "-jar", "./gooey-backend-0.0.1-SNAPSHOT.jar"]