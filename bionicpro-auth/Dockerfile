FROM bellsoft/liberica-openjdk-alpine:latest
RUN mkdir /opt/app
COPY bionicpro-auth-1.0-SNAPSHOT.jar /opt/app
EXPOSE 8084
ENV SERVER_ADDRESS=0.0.0.0
CMD ["java", "-jar", "/opt/app/bionicpro-auth-1.0-SNAPSHOT.jar", "--server.address=0.0.0.0"]