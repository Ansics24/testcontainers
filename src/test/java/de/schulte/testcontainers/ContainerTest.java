package de.schulte.testcontainers;

import static org.junit.Assert.assertTrue;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerTest {

    @Rule
    public GenericContainer postgres = new GenericContainer<>(DockerImageName.parse("postgres"));

    @ClassRule
    public static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis"));

    @Test
    public void firstContainerTest() {
        assertTrue(postgres.isRunning());
        assertTrue(redis.isRunning());
        System.out.println("Postgres Containername: " + postgres.getContainerName());
        System.out.println("Redis Containername: " + redis.getContainerName());
    }

    @Test
    public void secondContainerTest() {
        assertTrue(postgres.isRunning());
        assertTrue(redis.isRunning());
        System.out.println("Postgres Containername: " + postgres.getContainerName());
        System.out.println("Redis Containername: " + redis.getContainerName());
    }

}

