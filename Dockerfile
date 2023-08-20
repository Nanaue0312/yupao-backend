FROM java:openjdk-8u111-alpine as builder

# Copy local code to the container image.
WORKDIR /app


# Run the web service on container startup.
CMD ["nohup","java","-jar","/app/target/yupao-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod","&"]