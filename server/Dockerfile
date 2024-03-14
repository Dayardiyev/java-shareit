FROM amazoncorretto:11-alpine-jdk
COPY target/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]