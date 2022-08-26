package de.schulte.testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.zaxxer.hikari.HikariDataSource;

@Testcontainers
class TaskListServiceTest {

    @Container
    private GenericContainer postgres = new TasklistPostgresContainer();

    @Container
    private GenericContainer nginx =
            new GenericContainer(DockerImageName.parse("nginx:1.23")).withExposedPorts(80).dependsOn(postgres);

    private TaskListService serviceUnderTest;

    @BeforeEach
    void followContainerLogs() throws TimeoutException {
        postgres.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("Postgres Logger")));
        nginx.followOutput(new Slf4jLogConsumer(LoggerFactory.getLogger("Nginx Logger")));
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