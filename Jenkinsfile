pipeline {
    agent any
    tools {
        maven 'M3'
    }
    stages {
        stage('Prepare') {
            steps {
                sh '''
                    cp ${ENV_FOLDER}/*.properties ./src/main/resources
                '''
            }
        }
        stage('Test') {
            steps {
                sh '''
                    docker run -d --name db-test -p 27680:27017 --env-file ${ENV_FOLDER}/mongo-test.env mongo
                    sleep 30
                    mvn clean verify -Ptest
                    docker rm -f db-test
                '''
            }
        }
        stage('Sonar') {
            steps {
                sh "./sonar.sh ${SONAR_TOKEN}"
            }
        }
        stage('Package') {
            steps {
                sh "mvn clean install -DskipTests -Pprod -Djacoco.skip=true"
            }
        }
        stage('Build') {
            steps {
                sh '''
                    docker build --no-cache -t tericcabrel/parking:latest .
                '''
                // docker rmi -f $(docker images -q --filter dangling=true) fails if there is no dangling image
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                    docker-compose stop
                    docker-compose up
                '''
            }
        }
    }
    // The options directive is for configuration that applies to the whole job.
    options {
        // Make sure we only keep 3 builds at a time, so we don't fill up our storage!
        // buildDiscarder(logRotator(numToKeepStr:'3'))

        // And we'd really like to be sure that this build doesn't hang forever, so
        // let's time it out after an hour.
        timeout(time: 60, unit: 'MINUTES')
    }
}
