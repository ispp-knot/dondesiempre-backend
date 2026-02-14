FROM postgres:17.7

ENV POSTGRES_USER=devuser
ENV POSTGRES_PASSWORD=devpass
ENV POSTGRES_DB=devdb

COPY init.sql /docker-entrypoint-initdb.d/
