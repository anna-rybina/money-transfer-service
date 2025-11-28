package com.github.annarybina.moneytransfer.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransferIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void testHealthEndpointIntegration() {
        String url = "http://localhost:" + port + "/api/health";
        String response = restTemplate.getForObject(url, String.class);
        assertEquals("Money Transfer Service is UP!", response);
    }

    @Test
    void testTransferIntegration() {
        String url = "http://localhost:" + port + "/api/transfer";

        String requestJson = """
            {
                "cardFromNumber": "1234567812345678",
                "cardFromValidTill": "12/25",
                "cardFromCVV": "123",
                "cardToNumber": "8765432187654321",
                "amount": {
                    "value": 10000,
                    "currency": "RUB"
                }
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

        String response = restTemplate.postForObject(url, request, String.class);
        assertNotNull(response);
        assertTrue(response.contains("operationId"));
    }
}