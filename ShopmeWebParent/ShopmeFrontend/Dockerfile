FROM openjdk:17 AS build

LABEL authors="diaa"

WORKDIR /app

COPY "target/*.jar" /app/app.jar

FROM openjdk:17 AS runtime

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar" ,"app.jar"]