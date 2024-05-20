FROM eclipse-temurin:21-jdk-alpine

EXPOSE 8081
ADD build/libs/payment-mock-api-*.jar /opt/api.jar
ENTRYPOINT exec java $JAVA_OPTS $APPDYNAMICS -jar /opt/api.jar

