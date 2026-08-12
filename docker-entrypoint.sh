#!/bin/sh
set -e

if [ -f /run/secrets/db_password ]; then
  export SPRING_DATASOURCE_PASSWORD="$(tr -d '\r\n' < /run/secrets/db_password)"
fi

if [ -f /run/secrets/jwt_secret ]; then
  export APP_JWT_SECRET="$(tr -d '\r\n' < /run/secrets/jwt_secret)"
fi

exec java -jar /app/app.jar --spring.profiles.active=docker
