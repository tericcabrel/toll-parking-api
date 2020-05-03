#!/usr/bin/env bash

mvn sonar:sonar \
  -Dsonar.projectKey=toll-parking-library \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=$1