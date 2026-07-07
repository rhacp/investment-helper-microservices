#!/bin/sh
set -eu

psql -v ON_ERROR_STOP=1 \
  -v AUTH_DB_NAME="${AUTH_DB_NAME}" \
  -v USER_DB_NAME="${USER_DB_NAME}" \
  -v PORTFOLIO_DB_NAME="${PORTFOLIO_DB_NAME}" \
  -v MARKET_DATA_DB_NAME="${MARKET_DATA_DB_NAME}" \
  -v PREDICTION_DB_NAME="${PREDICTION_DB_NAME}" \
  -v ANALYTICS_DB_NAME="${ANALYTICS_DB_NAME}" \
  --username "${POSTGRES_USER}" \
  --dbname "${POSTGRES_DB}" \
  -f /docker-entrypoint-initdb.d/init-databases.sql
