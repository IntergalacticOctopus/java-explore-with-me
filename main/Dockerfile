FROM amazoncorretto:11-alpine-jdk as builder

RUN apk add --no-cache maven

COPY src /home/app/src

RUN mvn clean package -f /home/app/src/pom.xml

FROM amazoncorretto:11-alpine-jdk

COPY --from=builder /home/app/target/main.jar /main.jar

ENTRYPOINT ["java","-jar","/main.jar"]