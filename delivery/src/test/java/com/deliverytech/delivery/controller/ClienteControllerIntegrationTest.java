package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ClienteRequest;
import com.deliverytech.delivery.dto.response.ClienteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClienteControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/clientes";
    }

    private String getJwtToken() {
        // Registra e faz login como CLIENTE
        String email = "testuser@email.com";
        String senha = "testpass";
        // Registro
        String registerUrl = "http://localhost:" + port + "/api/auth/register";
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String json = String.format("{\"nome\":\"Test User\",\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(registerUrl, entity, String.class);
        // Login
        String loginJson = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        HttpEntity<String> loginEntity = new HttpEntity<>(loginJson, headers);
        ResponseEntity<String> loginResp = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        return loginResp.getBody();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getJwtToken());
        return headers;
    }

    @Test
    void testCadastrarEListarCliente() {
        ClienteRequest req = new ClienteRequest("Maria", "maria@email.com");
        HttpEntity<ClienteRequest> entity = new HttpEntity<>(req, authHeaders());
        ResponseEntity<ClienteResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, ClienteResponse.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Maria", resp.getBody().getNome());

        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<ClienteResponse[]> lista = restTemplate.exchange(getBaseUrl(), HttpMethod.GET, getEntity, ClienteResponse[].class);
        assertEquals(HttpStatus.OK, lista.getStatusCode());
        assertTrue(lista.getBody().length > 0);
    }

    @Test
    void testBuscarCliente_NaoEncontrado() {
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<ClienteResponse> resp = restTemplate.exchange(getBaseUrl() + "/9999", HttpMethod.GET, getEntity, ClienteResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    void testAtualizarCliente() {
        ClienteRequest req = new ClienteRequest("Carlos", "carlos@email.com");
        HttpEntity<ClienteRequest> entity = new HttpEntity<>(req, authHeaders());
        ResponseEntity<ClienteResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, ClienteResponse.class);
        Long id = resp.getBody().getId();
        ClienteRequest update = new ClienteRequest("Carlos Novo", "carlos.novo@email.com");
        HttpEntity<ClienteRequest> updateEntity = new HttpEntity<>(update, authHeaders());
        ResponseEntity<ClienteResponse> respUpdate = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, updateEntity, ClienteResponse.class);
        assertEquals(HttpStatus.OK, respUpdate.getStatusCode());
        assertEquals("Carlos Novo", respUpdate.getBody().getNome());
    }

    @Test
    void testAtivarDesativarCliente() {
        ClienteRequest req = new ClienteRequest("Ana", "ana@email.com");
        HttpEntity<ClienteRequest> entity = new HttpEntity<>(req, authHeaders());
        ResponseEntity<ClienteResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, ClienteResponse.class);
        Long id = resp.getBody().getId();
        HttpEntity<Void> patchEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<Void> respPatch = restTemplate.exchange(getBaseUrl() + "/" + id + "/status", HttpMethod.PATCH, patchEntity, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, respPatch.getStatusCode());
    }
} 