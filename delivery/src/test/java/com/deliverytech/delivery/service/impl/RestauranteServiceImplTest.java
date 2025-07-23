package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestauranteServiceImplTest {
    @Mock
    private RestauranteRepository restauranteRepository;
    @InjectMocks
    private RestauranteServiceImpl restauranteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarRestaurante() {
        Restaurante restaurante = Restaurante.builder().nome("Restaurante 1").build();
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(restaurante);
        Restaurante salvo = restauranteService.cadastrar(restaurante);
        assertEquals("Restaurante 1", salvo.getNome());
    }

    @Test
    void testBuscarPorId_Sucesso() {
        Restaurante restaurante = Restaurante.builder().id(1L).nome("Restaurante 1").build();
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        Optional<Restaurante> result = restauranteService.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("Restaurante 1", result.get().getNome());
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        when(restauranteRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Restaurante> result = restauranteService.buscarPorId(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testAtualizarRestaurante() {
        Restaurante original = Restaurante.builder().id(1L).nome("Restaurante 1").build();
        Restaurante atualizado = Restaurante.builder().nome("Restaurante Novo").build();
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(restauranteRepository.save(any(Restaurante.class))).thenReturn(atualizado);
        Restaurante salvo = restauranteService.atualizar(1L, atualizado);
        assertEquals("Restaurante Novo", salvo.getNome());
    }

    @Test
    void testAtualizarRestaurante_NaoEncontrado() {
        Restaurante atualizado = Restaurante.builder().nome("Restaurante Novo").build();
        when(restauranteRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> restauranteService.atualizar(2L, atualizado));
    }

    @Test
    void testBuscarPorCategoria() {
        Restaurante r1 = Restaurante.builder().id(1L).categoria("Japonesa").build();
        Restaurante r2 = Restaurante.builder().id(2L).categoria("Japonesa").build();
        when(restauranteRepository.findByCategoria("Japonesa")).thenReturn(Arrays.asList(r1, r2));
        List<Restaurante> lista = restauranteService.buscarPorCategoria("Japonesa");
        assertEquals(2, lista.size());
    }

    @Test
    void testListarTodos() {
        Restaurante r1 = Restaurante.builder().id(1L).build();
        Restaurante r2 = Restaurante.builder().id(2L).build();
        when(restauranteRepository.findAll()).thenReturn(Arrays.asList(r1, r2));
        List<Restaurante> lista = restauranteService.listarTodos();
        assertEquals(2, lista.size());
    }
} 