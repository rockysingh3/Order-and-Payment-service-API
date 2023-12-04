FROM openjdk:11



ARG JAR_FILE=target/*.jar


COPY ${JAR_FILE} cloudgatewat.jar

ENTRYPOINT ["java", "-jar", "/cloudgatewat.jar"]

EXPOSE 9090