package de.schulte.testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.zaxxer.hikari.HikariDataSource;

@Testcontainers
class TaskListServiceTest {

    @Container
    private GenericContainer postgres =
            new GenericContainer(DockerImageName.parse("postgres:12.12")).withEnv("POSTGRES_USER", "postgres")
                    .withEnv("POSTGRES_PASSWORD", "postgres").withEnv("POSTGRES_DB", "tasklist")
                    .withExposedPorts(5432);

    private TaskListService serviceUnderTest;

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