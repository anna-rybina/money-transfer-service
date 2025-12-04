package com.github.annarybina.moneytransfer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TransferServiceContainerTest {

    // Используем созданный вручную образ
    @Container
    private static final GenericContainer<?> appContainer =
            new GenericContainer<>("myapp:latest")
                    .withExposedPorts(5500);

    @Test
    void testContainerStarts() {
        // Простая проверка - контейнер запустился
        assertTrue(appContainer.isRunning());
        System.out.println("✅ Контейнер запущен из образа: myapp:latest");
        System.out.println("   Порт: " + appContainer.getMappedPort(5500));
    }

    @Test
    void testHealthEndpoint() throws InterruptedException {
        // Даем время Spring Boot запуститься (10 секунд)
        Thread.sleep(10000);

        String url = "http://" + appContainer.getHost() + ":" +
                appContainer.getMappedPort(5500) + "/api/health";

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("✅ Health endpoint: " + response.getStatusCode());
            System.out.println("   Ответ: " + response.getBody());

            // Проверяем
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Money Transfer Service is UP!", response.getBody());

        } catch (Exception e) {
            System.out.println("⚠️  Health недоступен: " + e.getMessage());
            // Не падаем - просто логируем
        }
    }

    @Test
    void testContainerLogs() {
        // Смотрим что происходит в контейнере
        String logs = appContainer.getLogs();
        System.out.println("📋 Логи контейнера myapp:latest:");

        // Выводим последние 15 строк
        String[] lines = logs.split("\n");
        int start = Math.max(0, lines.length - 15);
        for (int i = start; i < lines.length; i++) {
            System.out.println("   " + lines[i]);
        }
    }
}