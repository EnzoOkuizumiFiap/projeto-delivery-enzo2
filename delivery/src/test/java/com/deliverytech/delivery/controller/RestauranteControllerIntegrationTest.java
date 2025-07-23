package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestauranteControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/restaurantes";
    }

    private String getJwtToken() {
        String email = "restauranteuser@email.com";
        String senha = "restaurantepass";
        String registerUrl = "http://localhost:" + port + "/api/auth/register";
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String json = String.format("{\"nome\":\"Restaurante User\",\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"RESTAURANTE\",\"restauranteId\":1}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(registerUrl, entity, String.class);
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
    void testCadastrarEListarRestaurante() {
        RestauranteRequest req = new RestauranteRequest("Restaurante Teste", "Japonesa", "11999999999", new BigDecimal("5.0"), 45);
        HttpEntity<RestauranteRequest> entity = new HttpEntity<>(req, authHeaders());
        ResponseEntity<RestauranteResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, RestauranteResponse.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Restaurante Teste", resp.getBody().getNome());

        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<RestauranteResponse[]> lista = restTemplate.exchange(getBaseUrl(), HttpMethod.GET, getEntity, RestauranteResponse[].class);
        assertEquals(HttpStatus.OK, lista.getStatusCode());
        assertTrue(lista.getBody().length > 0);
    }

    @Test
    void testBuscarRestaurantePorId() {
        RestauranteRequest req = new RestauranteRequest("Restaurante Busca", "Italiana", "11888888888", new BigDecimal("7.0"), 50);
        HttpEntity<RestauranteRequest> entity = new HttpEntity<>(req, authHeaders());
        ResponseEntity<RestauranteResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, RestauranteResponse.class);
        Long id = resp.getBody().getId();
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<RestauranteResponse> respGet = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.GET, getEntity, RestauranteResponse.class);
        assertEquals(HttpStatus.OK, respGet.getStatusCode());
        assertEquals("Restaurante Busca", respGet.getBody().getNome());
    }

    @Test
    void testBuscarRestaurantePorCategoria() {
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<RestauranteResponse[]> resp = restTemplate.exchange(getBaseUrl() + "/categoria/Japonesa", HttpMethod.GET, getEntity, RestauranteResponse[].class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testAtualizarRestaurante() {
        RestauranteRequest req = new RestauranteRequest("Restaurante Atualiza", "Chinesa", "11777777777", new BigDecimal("8.0"), 60);
        HttpEntity<RestauranteRequest> entity = new HttpEntity<>(req, authHeaders());
        ResponseEntity<RestauranteResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, RestauranteResponse.class);
        Long id = resp.getBody().getId();
        RestauranteRequest update = new RestauranteRequest("Restaurante Atualizado", "Chinesa", "11777777777", new BigDecimal("10.0"), 70);
        HttpEntity<RestauranteRequest> updateEntity = new HttpEntity<>(update, authHeaders());
        ResponseEntity<RestauranteResponse> respUpdate = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, updateEntity, RestauranteResponse.class);
        assertEquals(HttpStatus.OK, respUpdate.getStatusCode());
        assertEquals("Restaurante Atualizado", respUpdate.getBody().getNome());
    }

    @Test
    void testRestauranteNaoEncontrado() {
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<RestauranteResponse> resp = restTemplate.exchange(getBaseUrl() + "/9999", HttpMethod.GET, getEntity, RestauranteResponse.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), resp.getStatusCode().value());
    }
} 