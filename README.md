## Toll Parking Library
The REST API for cars toll parking management

## Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Profiles](#profiles)
- [Installation](#installation)
- [Data Seed](#data-seed)
- [Tests](#tests)
- [Deployment](#deployment)


## Features
This features are available in this service

- **Create car's type: three types are created at application startup**
- **Create parking slot and define a pricing policy per item**
- **Register customer with his type of car:**
- **Create car recharge session in a parking slot requested by a customer**
- **Send email to the customer with the details of the car recharge**
- **Authentication of User and Admin with JWT (Customer can't login, they are created by the User)**
- **Role based authorization: Allow or denied access to a resource according to the role (User can creat bute only Admin can delete)**
- **Swagger: API Documentation with Swagger 2**
- **HTML Email: Use Thymeleaf to build HTML templates for email**
- **A route (/template) to edit and view the HTML template for email**

## Prerequisites
- JDK 8
- Maven
- MongoDB
- SonarQube
- Docker
- Jenkins

## Profiles
The project has three Maven profiles:
- **Dev:** For local development. It's activated by default but you can edit in pom.xml on tag profiles
- **Test** For Unit and Integration tests, the file name must be application-test.properties
- **Prod** For Production, this file is used to build the jar. the file name must be application-prod.properties
None of these files are versioned, so you need to create them from application-example.properties and update the config

## Installation
- Clone the repository
```bash
$ git clone https://github.com/tericcabrel/toll-parking-api.git [project_name]
```

- Install dependencies
```bash
$ cd [project_name]
$ mvn install
```
- Create the configuration file and update with your local config
```bash
$ cd src/main/resources
$ cp application-example.properties application-dev.properties
$ nano application-dev.properties
```
Repeat the action above to create application-test.properties and application-prod.properties

- Start Application
```bash
$ mvn spring-boot:run
```
Note: An IDE like **IntelliJ** can perform these tasks for you automatically

## Data Seed
Inside the package **com.tericcabrel.parking.bootstrap**, the file named _DataSeeder.java_ 
is responsible for loading data on application startup. 2 Roles, 1 Admin user and 3 car's type are inserted

## Tests
The has 142 tests divided in two sections:
- 57 unit tests
- 85 integration tests
- Coverage Percentage: **96.8%**


The coverage is generated with JaCoCo and the result is parsed by SonarQube.<br> 

Steps to reproduce: We assume SonarQube is installed and run on port 9000:<br>
**Login then create a Java project with the key "toll-parking-library" and generate the token**
```bash
mvn clean verify -Ptest
./sonar.sh <sonar_token_generated_here>
```
When completed, you will see the coverage report in SonarQube

## Deployment
Jenkins and docker are combined to deploy the app on server following the CI process. It can be hard to
setup in local because the environment variable named _ENV_FOLDER_ in docker-compose.yml come from Jenkins pipeline
but i will give the instructions to run the project with Docker.

Before run these commands, **make sure the DB parameters in application-prod.properties are the same with mongo.env**.
The value of spring.data.mongodb.host must be mongodb not localhost and the port remain 27017

```bash
mvn clean install -DskipTests -Pprod -Djacoco.skip=true
docker build --no-cache -t tericcabrel/parking:latest .
export ENV_FOLDER=./
docker-compose up
```
Browse the URL: http://localhost:8680

The application deployed on the server with Jenkins and Docker is available here : [https://parking.tericcabrel.com](https://parking.tericcabrel.com)

