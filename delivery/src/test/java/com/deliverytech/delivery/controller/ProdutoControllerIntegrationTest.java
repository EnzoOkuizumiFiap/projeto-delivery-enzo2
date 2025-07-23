package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProdutoControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/produtos";
    }

    private Long cadastrarRestauranteERetornarId() {
        String url = "http://localhost:" + port + "/api/restaurantes";
        // Usuário ADMIN para cadastrar restaurante
        String email = "admin@email.com";
        String senha = "adminpass";
        String registerUrl = "http://localhost:" + port + "/api/auth/register";
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String json = String.format("{\"nome\":\"Admin\",\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"ADMIN\"}", email, senha);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(registerUrl, entity, String.class);
        String loginJson = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        HttpEntity<String> loginEntity = new HttpEntity<>(loginJson, headers);
        ResponseEntity<String> loginResp = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        String token = loginResp.getBody();
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);
        RestauranteRequest req = new RestauranteRequest("Restaurante Teste", "Japonesa", "11999999999", new java.math.BigDecimal("5.0"), 45);
        HttpEntity<RestauranteRequest> restEntity = new HttpEntity<>(req, authHeaders);
        ResponseEntity<RestauranteResponse> resp = restTemplate.postForEntity(url, restEntity, RestauranteResponse.class);
        return resp.getBody().getId();
    }

    private String getJwtToken(Long restauranteId) {
        String email = "produtouser@email.com";
        String senha = "produtopass";
        String registerUrl = "http://localhost:" + port + "/api/auth/register";
        String loginUrl = "http://localhost:" + port + "/api/auth/login";
        String json = String.format("{\"nome\":\"Produto User\",\"email\":\"%s\",\"senha\":\"%s\",\"role\":\"RESTAURANTE\",\"restauranteId\":%d}", email, senha, restauranteId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForEntity(registerUrl, entity, String.class);
        String loginJson = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", email, senha);
        HttpEntity<String> loginEntity = new HttpEntity<>(loginJson, headers);
        ResponseEntity<String> loginResp = restTemplate.postForEntity(loginUrl, loginEntity, String.class);
        return loginResp.getBody();
    }

    private HttpHeaders authHeaders(Long restauranteId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getJwtToken(restauranteId));
        return headers;
    }

    @Test
    void testCadastrarEListarProduto() {
        Long restauranteId = cadastrarRestauranteERetornarId();
        ProdutoRequest req = new ProdutoRequest("Sushi", "Japonesa", "Sushi de salmão", new java.math.BigDecimal("25.0"), restauranteId);
        HttpEntity<ProdutoRequest> entity = new HttpEntity<>(req, authHeaders(restauranteId));
        ResponseEntity<ProdutoResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, ProdutoResponse.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals("Sushi", resp.getBody().getNome());
    }

    @Test
    void testListarPorRestaurante() {
        Long restauranteId = cadastrarRestauranteERetornarId();
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders(restauranteId));
        ResponseEntity<ProdutoResponse[]> resp = restTemplate.exchange(getBaseUrl() + "/restaurante/" + restauranteId, HttpMethod.GET, getEntity, ProdutoResponse[].class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testAtualizarProduto() {
        Long restauranteId = cadastrarRestauranteERetornarId();
        ProdutoRequest req = new ProdutoRequest("Temaki", "Japonesa", "Temaki de salmão", new java.math.BigDecimal("20.0"), restauranteId);
        HttpEntity<ProdutoRequest> entity = new HttpEntity<>(req, authHeaders(restauranteId));
        ResponseEntity<ProdutoResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, ProdutoResponse.class);
        Long id = resp.getBody().getId();
        ProdutoRequest update = new ProdutoRequest("Temaki Especial", "Japonesa", "Temaki de atum", new java.math.BigDecimal("22.0"), restauranteId);
        HttpEntity<ProdutoRequest> updateEntity = new HttpEntity<>(update, authHeaders(restauranteId));
        ResponseEntity<ProdutoResponse> respUpdate = restTemplate.exchange(getBaseUrl() + "/" + id, HttpMethod.PUT, updateEntity, ProdutoResponse.class);
        assertEquals(HttpStatus.OK, respUpdate.getStatusCode());
        assertEquals("Temaki Especial", respUpdate.getBody().getNome());
    }

    @Test
    void testAlterarDisponibilidade() {
        Long restauranteId = cadastrarRestauranteERetornarId();
        ProdutoRequest req = new ProdutoRequest("Yakissoba", "Chinesa", "Yakissoba de carne", new java.math.BigDecimal("30.0"), restauranteId);
        HttpEntity<ProdutoRequest> entity = new HttpEntity<>(req, authHeaders(restauranteId));
        ResponseEntity<ProdutoResponse> resp = restTemplate.postForEntity(getBaseUrl(), entity, ProdutoResponse.class);
        Long id = resp.getBody().getId();
        HttpEntity<Void> patchEntity = new HttpEntity<>(authHeaders(restauranteId));
        ResponseEntity<Void> respPatch = restTemplate.exchange(getBaseUrl() + "/" + id + "/disponibilidade?disponivel=false", HttpMethod.PATCH, patchEntity, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, respPatch.getStatusCode());
    }

    @Test
    void testProdutoNaoEncontrado() {
        Long restauranteId = cadastrarRestauranteERetornarId();
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders(restauranteId));
        ResponseEntity<ProdutoResponse> resp = restTemplate.exchange(getBaseUrl() + "/9999", HttpMethod.GET, getEntity, ProdutoResponse.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), resp.getStatusCode().value());
    }
} 