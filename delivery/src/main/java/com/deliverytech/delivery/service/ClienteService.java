package com.deliverytech.delivery.service;

import com.deliverytech.delivery.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    Cliente cadastrar(Cliente cliente);
    Optional<Cliente> buscarPorId(Long id);
    List<Cliente> listarAtivos();
    Cliente atualizar(Long id, Cliente clienteAtualizado);
    void ativarDesativar(Long id);
}
