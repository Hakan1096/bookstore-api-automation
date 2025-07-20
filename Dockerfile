FROM maven:3.9.4-eclipse-temurin-11-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY testng.xml .
COPY allure.properties .

RUN mvn clean compile test-compile -DskipTests=true

FROM eclipse-temurin:11-jre-alpine

RUN apk add --no-cache curl bash tzdata
ENV TZ=UTC

WORKDIR /app

COPY --from=build /root/.m2 /root/.m2
COPY --from=build /app/target /app/target
COPY --from=build /app/src /app/src
COPY --from=build /app/pom.xml /app/
COPY --from=build /app/testng.xml /app/
COPY --from=build /app/allure.properties /app/

RUN mkdir -p target/{allure-results,allure-report,surefire-reports}

ENV API_BASE_URL=https://fakerestapi.azurewebsites.net
ENV PARALLEL_THREADS=3
ENV TEST_SUITE=testng.xml

COPY docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
CMD ["run-tests"]
