#!/bin/sh
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
JAVA_OPTS="${JAVA_OPTS:-}"
exec "$JAVA_HOME/bin/java" $JAVA_OPTS -jar "$GRADLE_USER_HOME/wrapper/dists/gradle-8.4-bin/*/gradle-8.4/lib/gradle-launcher-8.4.jar" "$@"
