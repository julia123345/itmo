FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /app

COPY auth-service/build.gradle auth-service/settings.gradle ./auth-service/
RUN gradle --no-daemon -p auth-service dependencies || true

COPY auth-service/src ./auth-service/src
RUN gradle --no-daemon -p auth-service clean build

FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17
USER root

COPY --from=build /app/auth-service/build/libs/auth.war /opt/jboss/wildfly/standalone/deployments/auth.war

COPY auth-service/entrypoint.sh /tmp/entrypoint.sh
RUN chmod +x /tmp/entrypoint.sh

RUN chown -R jboss:jboss /opt/jboss/wildfly

USER jboss
EXPOSE 8080 9990

CMD ["/tmp/entrypoint.sh"]

