FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /app

COPY geometry-service/build.gradle geometry-service/settings.gradle ./geometry-service/
RUN gradle --no-daemon -p geometry-service dependencies || true

COPY geometry-service/src ./geometry-service/src
RUN gradle --no-daemon -p geometry-service clean build

FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17
USER root

COPY --from=build /app/geometry-service/build/libs/geometry.war /opt/jboss/wildfly/standalone/deployments/geometry.war

COPY geometry-service/entrypoint.sh /tmp/entrypoint.sh
RUN chmod +x /tmp/entrypoint.sh

RUN chown -R jboss:jboss /opt/jboss/wildfly

USER jboss
EXPOSE 8080 9990

CMD ["/tmp/entrypoint.sh"]

