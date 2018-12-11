FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/landschaften.jar /landschaften/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/landschaften/app.jar"]
