# select image
FROM maven:3.5-jdk-8

# copy your source tree
COPY ./ ./

# build for release
RUN mvn clean package

# set the startup command to run your binary
CMD ["java", "-jar", "./target/kafka-streams-wordcount.jar"]