package com.github.annarybina.moneytransfer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class MoneyTransferContainerTest {

    // Используем наш Docker образ
    @Container
    private static final GenericContainer<?> appContainer =
            new GenericContainer<>("money-transfer-service:1.0")
                    .withExposedPorts(8080);

    @Test
    void testContainerStartsAndHealthEndpointWorks() {
        // Даем время приложению запуститься
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Получаем динамический порт
        String baseUrl = "http://" + appContainer.getHost() + ":" +
                appContainer.getMappedPort(8080);

        RestTemplate restTemplate = new RestTemplate();

        // Проверяем health endpoint
        ResponseEntity<String> healthResponse = restTemplate.getForEntity(
                baseUrl + "/health", String.class);

        assertEquals(200, healthResponse.getStatusCode().value());
        assertEquals("Money Transfer Service is UP!", healthResponse.getBody());

        System.out.println("✅ Контейнер запущен и health endpoint работает");
        System.out.println("   URL: " + baseUrl);
    }

    @Test
    void testTransferInContainer() {
        // Ждем запуска Spring Boot
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String baseUrl = "http://" + appContainer.getHost() + ":" +
                appContainer.getMappedPort(8080);
        RestTemplate restTemplate = new RestTemplate();

        // Тестируем перевод
        String transferJson = """
                {
                    "cardFromNumber": "1111222233334444",
                    "cardFromValidTill": "12/25",
                    "cardFromCVV": "123",
                    "cardToNumber": "5555666677778888",
                    "amount": {
                        "value": 3000,
                        "currency": "RUB"
                    }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(transferJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/transfer", request, String.class);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("operationId"));

        System.out.println("✅ Перевод в контейнере работает");
        System.out.println("   Ответ: " + response.getBody());
    }

    @Test
    void testContainerLogs() {
        // Просто проверяем что контейнер запущен и логи доступны
        assertTrue(appContainer.isRunning());

        // Можно посмотреть логи контейнера (первые 10 строк)
        String logs = appContainer.getLogs();
        System.out.println("📋 Логи контейнера:");
        String[] lines = logs.split("\n");
        int linesToShow = Math.min(10, lines.length);
        for (int i = 0; i < linesToShow; i++) {
            System.out.println("   " + lines[i]);
        }
    }
}
