#!/usr/bin/env bash

mvn sonar:sonar \
  -Dsonar.projectKey=toll-parking-library \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=99aa463c23723e801e091536de3c9ecae42b95b7