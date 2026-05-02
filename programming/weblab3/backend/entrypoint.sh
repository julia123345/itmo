#!/bin/bash
# Hibernate RESOURCE_LOCAL + MySQL в WAR — datasource WildFly не нужен
exec /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
