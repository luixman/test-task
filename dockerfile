FROM maven:3.8-openjdk-17 AS maven_build
EXPOSE 8081
WORKDIR java
COPY ./ ./
RUN mvn clean package -DskipTests

FROM alvistack/openjdk-17 AS test_task
COPY --from=maven_build java/target/test-task-1.0.jar test-task.jar
COPY --from=maven_build java/db db


ENTRYPOINT ["java","-jar", "test-task.jar"]