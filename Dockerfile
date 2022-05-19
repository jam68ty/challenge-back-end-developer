FROM openjdk:11
COPY ./target/*.jar ebanking-backend.jar
ENTRYPOINT ["java","-jar","/ebanking-backend.jar"]
EXPOSE 8080