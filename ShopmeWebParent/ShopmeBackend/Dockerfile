FROM openjdk:17 AS build

LABEL authors="diaa"

WORKDIR /app

COPY "target/ShopmeBackend-0.0.1-SNAPSHOT.jar" app.jar

FROM openjdk:17 as runtime

LABEL authors="diaa"

RUN groupadd -r appuser && useradd -r -g appuser appuser

USER appuser

WORKDIR /app

COPY --from=build /app/app.jar /app/app.jar

WORKDIR /app

EXPOSE 8083

ENTRYPOINT ["java", "-jar" ,"app.jar"]