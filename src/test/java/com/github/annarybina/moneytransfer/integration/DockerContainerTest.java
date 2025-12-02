package com.github.annarybina.moneytransfer.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class DockerContainerTest {

    @Container
    private final GenericContainer<?> alpineContainer = new GenericContainer<>(
            DockerImageName.parse("alpine:latest"))  // образ Docker
            .withCommand("sleep", "infinity");  // команда для запуска

    @Test
    void testContainerStartsSuccessfully() {
        assertTrue(alpineContainer.isRunning(), "Контейнер должен быть запущен");

        System.out.println("Контейнер ID: " + alpineContainer.getContainerId());
        System.out.println("Образ: " + alpineContainer.getDockerImageName());
    }

    @Test
    void testContainerHasCorrectImage() {
        assertEquals("alpine:latest", alpineContainer.getDockerImageName());
    }
}
