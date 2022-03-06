FROM amazoncorretto:11
COPY build/libs/bm-web-app*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app.jar"]