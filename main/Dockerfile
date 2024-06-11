FROM amazoncorretto:11-alpine-jdk as builder

RUN apk add --no-cache maven

COPY src /home/app/src

FROM amazoncorretto:11-alpine-jdk

COPY --from=builder /home/app/target/main.jar /main.jar

ENTRYPOINT ["java","-jar","/main.jar"]