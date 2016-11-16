FROM postgres:latest

MAINTAINER Vilmos Nagy <vilmos.nagy@outlook.com>

COPY init/* /docker-entrypoint-initdb.d/

ENV POSTGRES_PASSWORD='password'