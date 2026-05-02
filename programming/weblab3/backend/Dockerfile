FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
RUN gradle --no-daemon dependencies || true

COPY src ./src
RUN gradle --no-daemon clean build


FROM quay.io/wildfly/wildfly:27.0.1.Final-jdk17
USER root

COPY --from=build /app/build/libs/backend.war /opt/jboss/wildfly/standalone/deployments/backend.war

COPY entrypoint.sh /tmp/entrypoint.sh
RUN chmod +x /tmp/entrypoint.sh

RUN chown -R jboss:jboss /opt/jboss/wildfly

USER jboss
EXPOSE 8080 9990

CMD ["/tmp/entrypoint.sh"]
