package de.schulte.testcontainers;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

public class TasklistPostgresContainer extends PostgreSQLContainer {

    public TasklistPostgresContainer() {
        super("postgres:14-alpine");
        this.withUsername("postgres")
                .withPassword("postgres")
                .withDatabaseName("tasklist")
                .withClasspathResourceMapping("postgres/PostgresInit.sql", "/docker-entrypoint-initdb.d/Init.sql", BindMode.READ_ONLY)
                .withExposedPorts(5432)
                .waitingFor(new WaitAllStrategy()
                        .withStrategy(Wait.forLogMessage(".*is ready to accept connections.*", 1))
                        .withStrategy(Wait.defaultWaitStrategy()));

    }
}
