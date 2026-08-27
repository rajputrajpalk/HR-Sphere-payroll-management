FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 10000

CMD ["java", "-jar", "target/hr-sphere-0.1.0-SNAPSHOT.jar"]