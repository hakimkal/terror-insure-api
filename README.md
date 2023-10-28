# Terron

Terron is a system built using Java and Spring Boot and Postgres Database.

# Prerequisites

In order to run this application you need to install these tools: 
 - java
 - maven
 - docker
 - postgres

How to run it?

The entire application can be run with a single command in a terminal:

$ /bin/zsh /Users/user/terronApi/run-docker.sh

After running the app it can be accessed using this connector:

Host: localhost:8000 Database: terrondb

This is a Multi Module Spring Boot (Java) based application that comprises the data, service, security and web module. 
It connects with a database and expose the REST endpoints that can be consumed by the frontend. It supports multiple HTTP POST methods.

A full list of available REST endpoints could be found in Postman Documentation, which could be called using the link:
http://64.227.34.103:8000/swagger-ui/index.html#/
