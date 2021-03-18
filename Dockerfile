FROM openjdk:11-jre-slim
VOLUME /tmp
ARG JAR_FILE=build/libs/VetApp-0.0.1-SNAPSHOT.jar
ARG DB_URI
ARG DB_USER
ARG DB_PASSWORD
ENV DATABASE_URI=$DB_URI
ENV DATABASE_USER=$DB_USER
ENV DATABASE_PASSWORD=$DB_PASSWORD
COPY ${JAR_FILE} app.jar
CMD java -Djava.security.egd=file:/dev/./urandom -Dserver.port=$PORT -Dspring.profiles.active=prod -jar /app.jar