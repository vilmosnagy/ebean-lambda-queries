#!/usr/bin/env bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER ebean WITH ENCRYPTED PASSWORD 'ebean';
    CREATE DATABASE ebean;
    GRANT ALL PRIVILEGES ON DATABASE ebean TO ebean;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -d ebean < /docker-entrypoint-initdb.d/chinookDataset
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -d ebean <<-EOSQL
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ebean;
EOSQL