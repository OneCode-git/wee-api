FROM --platform=$BUILDPLATFORM alpine:3.8 as downloader
RUN apk update && \
    apk add unzip && \
    apk add curl && \
    curl --location https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip  > newrelic-java.zip && \
    unzip -q newrelic-java.zip
    

FROM --platform=$BUILDPLATFORM  arm64v8/amazoncorretto:11.0.18
WORKDIR /app
COPY --from=downloader newrelic/newrelic.jar /var/
COPY build/libs/wee-api-0.0.1-SNAPSHOT.jar /app
ENV PATH="${PATH}:/app"
EXPOSE 5007
ENTRYPOINT ["java","-javaagent:/var/newrelic.jar", "-jar","/app/wee-api-0.0.1-SNAPSHOT.jar"]