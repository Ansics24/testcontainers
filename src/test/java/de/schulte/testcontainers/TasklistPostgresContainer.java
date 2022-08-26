package de.schulte.testcontainers;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class TasklistPostgresContainer extends GenericContainer<TasklistPostgresContainer> {

    public TasklistPostgresContainer() {
        super(new ImageFromDockerfile("tasklist-postgres-container").withFileFromClasspath(
                "Dockerfile", "postgres/Dockerfile").withFileFromClasspath("PostgresInit.sql",
                "postgres/PostgresInit.sql"));
        this.withEnv("POSTGRES_USER", "postgres")
                .withEnv("POSTGRES_PASSWORD", "postgres")
                .withEnv("POSTGRES_DB", "tasklist")
                .withClasspathResourceMapping("postgres/PostgresInit.sql", "/docker-entrypoint-initdb.d/Init.sql",
                        BindMode.READ_ONLY)
                .withExposedPorts(5432)
                .waitingFor(new WaitAllStrategy()
                        .withStrategy(Wait.forLogMessage(".*is ready to accept connections.*", 1))
                        .withStrategy(Wait.defaultWaitStrategy()));
    }
}
