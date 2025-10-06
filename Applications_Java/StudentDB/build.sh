#!/bin/bash
mvn clean package
java -jar target/studentdb-maven-javafx-1.0.0-shaded.jar
