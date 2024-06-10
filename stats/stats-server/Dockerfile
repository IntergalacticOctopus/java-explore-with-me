FROM amazoncorretto:17-alpine-jdk
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java","-jar","/stats-server.jar"]