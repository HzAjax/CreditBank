FROM bellsoft/liberica-openjre-alpine:21.0.5
COPY build/libs/deal-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
