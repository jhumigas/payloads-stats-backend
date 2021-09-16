FROM adoptopenjdk:11-jre-hotspot
ENV PORT 8080
EXPOSE 8080

ARG JAR_FILE=*.jar
COPY target/${JAR_FILE} /opt/application.jar
WORKDIR /opt
CMD ["java", "-jar", "application.jar"]