FROM openjdk:11-jdk
VOLUME /tmp
EXPOSE 8080
ADD target/luizalab-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=container", "-jar", "app.jar"]