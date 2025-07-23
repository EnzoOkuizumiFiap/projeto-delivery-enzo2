package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.service.RestauranteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProdutoServiceImplTest {
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private RestauranteService restauranteService;
    @InjectMocks
    private ProdutoServiceImpl produtoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarProduto() {
        Restaurante restaurante = Restaurante.builder().id(1L).build();
        Produto produto = Produto.builder().nome("Sushi").preco(BigDecimal.TEN).restaurante(restaurante).build();
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        Produto salvo = produtoService.cadastrar(produto);
        assertEquals("Sushi", salvo.getNome());
    }

    @Test
    void testBuscarPorId_Sucesso() {
        Produto produto = Produto.builder().id(1L).nome("Sushi").build();
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        Optional<Produto> result = produtoService.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("Sushi", result.get().getNome());
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        when(produtoRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Produto> result = produtoService.buscarPorId(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testBuscarPorRestaurante() {
        Produto p1 = Produto.builder().id(1L).restaurante(Restaurante.builder().id(1L).build()).build();
        Produto p2 = Produto.builder().id(2L).restaurante(Restaurante.builder().id(1L).build()).build();
        when(produtoRepository.findByRestauranteId(1L)).thenReturn(Arrays.asList(p1, p2));
        List<Produto> produtos = produtoService.buscarPorRestaurante(1L);
        assertEquals(2, produtos.size());
    }

    @Test
    void testAtualizarProduto() {
        Produto original = Produto.builder().id(1L).nome("Sushi").build();
        Produto atualizado = Produto.builder().nome("Sashimi").build();
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(original));
        when(produtoRepository.save(any(Produto.class))).thenReturn(atualizado);
        Produto salvo = produtoService.atualizar(1L, atualizado);
        assertEquals("Sashimi", salvo.getNome());
    }

    @Test
    void testAtualizarProduto_NaoEncontrado() {
        Produto atualizado = Produto.builder().nome("Sashimi").build();
        when(produtoRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> produtoService.atualizar(2L, atualizado));
    }

    @Test
    void testAlterarDisponibilidade() {
        Produto produto = Produto.builder().id(1L).disponivel(true).build();
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        assertDoesNotThrow(() -> produtoService.alterarDisponibilidade(1L, false));
    }

    @Test
    void testAlterarDisponibilidade_NaoEncontrado() {
        when(produtoRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> produtoService.alterarDisponibilidade(2L, false));
    }
} 