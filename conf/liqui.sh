#!/bin/bash

java -jar /home/frincon/tmp/liquibase/liquibase.jar --changeLogFile=$1 update
