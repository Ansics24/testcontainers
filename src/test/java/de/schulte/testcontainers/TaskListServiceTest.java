package de.schulte.testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.DockerComposeContainer;
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
    private DockerComposeContainer environment = new DockerComposeContainer(
            new File("src/test/resources/docker-compose.yml"))
            .withExposedService("postgres", 5432)
            .withExposedService("nginx", 80);

    private TaskListService serviceUnderTest;

    @BeforeEach
    void setUp() {
        final var postgresHost = environment.getServiceHost("postgres", 5432);
        final var postgresPort = environment.getServicePort("postgres", 5432);
        final var postgresUrl = String.format("jdbc:postgresql://%s:%d/tasklist", postgresHost, postgresPort);

        final var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(postgresUrl);
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