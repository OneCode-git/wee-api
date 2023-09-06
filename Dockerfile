FROM --platform=$BUILDPLATFORM alpine:latest as downloader
RUN apk add --no-cache tzdata
ENV TZ="Asia/Kolkata"

RUN apk update && \
    apk add unzip && \
    apk add curl && \
    curl --location https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip  > newrelic-java.zip && \
    unzip -q newrelic-java.zip
    

FROM --platform=$BUILDPLATFORM  arm64v8/amazoncorretto:11.0.20-alpine
RUN apk add --no-cache tzdata
ENV TZ="Asia/Kolkata"

WORKDIR /app
COPY --from=downloader newrelic/newrelic.jar /var/
COPY build/libs/wee-api-0.0.1-SNAPSHOT.jar /app
ENV PATH="${PATH}:/app"
EXPOSE 5007
#ENV newrelic.config.app_name lms-staging-arm
#ENV newrelic.config.license_key 68091cd17960f5b4a1ea1ca600708bc38876NRAL

ENTRYPOINT ["java","-javaagent:/var/newrelic.jar", "-jar","/app/wee-api-0.0.1-SNAPSHOT.jar"]
