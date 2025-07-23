package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.model.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
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

class ClienteServiceImplTest {
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCadastrarCliente() {
        Cliente cliente = Cliente.builder().nome("João").email("joao@email.com").build();
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        Cliente salvo = clienteService.cadastrar(cliente);
        assertEquals("João", salvo.getNome());
        assertEquals("joao@email.com", salvo.getEmail());
    }

    @Test
    void testBuscarPorId_Sucesso() {
        Cliente cliente = Cliente.builder().id(1L).nome("João").build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        Optional<Cliente> result = clienteService.buscarPorId(1L);
        assertTrue(result.isPresent());
        assertEquals("João", result.get().getNome());
    }

    @Test
    void testBuscarPorId_NaoEncontrado() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Cliente> result = clienteService.buscarPorId(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testListarAtivos() {
        Cliente c1 = Cliente.builder().id(1L).ativo(true).build();
        Cliente c2 = Cliente.builder().id(2L).ativo(true).build();
        when(clienteRepository.findByAtivoTrue()).thenReturn(Arrays.asList(c1, c2));
        List<Cliente> ativos = clienteService.listarAtivos();
        assertEquals(2, ativos.size());
    }

    @Test
    void testAtualizarCliente() {
        Cliente original = Cliente.builder().id(1L).nome("João").email("joao@email.com").build();
        Cliente atualizado = Cliente.builder().nome("João Novo").email("joao.novo@email.com").build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(original));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(atualizado);
        Cliente salvo = clienteService.atualizar(1L, atualizado);
        assertEquals("João Novo", salvo.getNome());
        assertEquals("joao.novo@email.com", salvo.getEmail());
    }

    @Test
    void testAtualizarCliente_NaoEncontrado() {
        Cliente atualizado = Cliente.builder().nome("João Novo").email("joao.novo@email.com").build();
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clienteService.atualizar(2L, atualizado));
    }

    @Test
    void testAtivarDesativar() {
        Cliente cliente = Cliente.builder().id(1L).ativo(true).build();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        assertDoesNotThrow(() -> clienteService.ativarDesativar(1L));
    }

    @Test
    void testAtivarDesativar_NaoEncontrado() {
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clienteService.ativarDesativar(2L));
    }
} 