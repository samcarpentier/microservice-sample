#!/bin/bash

mvn clean -U install

mvn exec:java -pl login-gateway &
mvn exec:java -pl authentication-ws

