FROM openjdk:15-jdk AS app-build

ENV GRADLE_OPTS -Dorg,gradle,deamon=false
COPY . /build
WORKDIR /build
RUN ./gradlew build