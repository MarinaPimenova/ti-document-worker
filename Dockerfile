FROM alpine/java:21-jre

LABEL org.opencontainers.image.source=https://github.com/MarinaPimenova/ti-document-worker

COPY build/libs/*.jar /app.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app.jar"]
