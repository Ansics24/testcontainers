package de.schulte.testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.zaxxer.hikari.HikariDataSource;

@Testcontainers
class TaskListServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListServiceTest.class);

    @Container
    private GenericContainer postgres =
            new GenericContainer(DockerImageName.parse("postgres:12.12")).withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "postgres")
                    .withEnv("POSTGRES_DB", "tasklist")
                    .withClasspathResourceMapping("PostgresInit.sql", "/docker-entrypoint-initdb.d/Init.sql",
                            BindMode.READ_ONLY)
                    .withExposedPorts(5432);

    private TaskListService serviceUnderTest;

    @BeforeEach
    void followContainerLogs() throws TimeoutException {
        postgres.followOutput(new Slf4jLogConsumer(LOGGER));
    }

    @BeforeEach
    void setUp() {
        final var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:" + postgres.getMappedPort(5432) + "/tasklist");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        serviceUnderTest = new TaskListService(dataSource);
    }

    @Test
    void shouldInsertNewEntries() {
        final var entry = serviceUnderTest.insert(new TaskListEntry("write tests"));
        assertNotNull(entry);
        assertNotNull(entry.getId());
    }
}