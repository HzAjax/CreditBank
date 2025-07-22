FROM bellsoft/liberica-openjre-alpine:21.0.5
COPY build/libs/dossier-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
