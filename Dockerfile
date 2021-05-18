FROM maven:3.8.1-jdk-11
COPY . /usr/src/valkyr-ie
WORKDIR /usr/src/valkyr-ie
RUN mvn clean install
EXPOSE 8080
CMD ["java", "-jar", "target/valkyr-ie-gate-1.0.jar"]
