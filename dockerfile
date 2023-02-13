
FROM radut/openjdk-17 AS test_task
COPY target/test-task-1.0.jar test-task.jar
COPY db db


ENTRYPOINT ["java","-jar", "test-task.jar"]