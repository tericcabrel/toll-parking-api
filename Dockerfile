FROM openjdk:8

RUN mkdir -p /var/app && chmod -R 777 /var/app

WORKDIR /var/app

COPY target/parking-0.0.1-SNAPSHOT.jar app.jar
COPY ./wait-for-it.sh ./

RUN chmod +x /var/app/wait-for-it.sh

EXPOSE 8680

CMD ["./wait-for-it.sh", "mongodb:27017", "-t", "15", "--", "java", "-jar", "app.jar"]

# docker build --no-cache -t tericcabrel/parking:latest .