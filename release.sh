#!/bin/sh
./mvnw release:clean -DskipPitest -DskipTests -Darguments=-DskipTests
./mvnw release:prepare -DskipPitest -DskipTests -Darguments=-DskipTests
./mvnw release:perform -DskipPitest -DskipTests -Darguments=-DskipTests

