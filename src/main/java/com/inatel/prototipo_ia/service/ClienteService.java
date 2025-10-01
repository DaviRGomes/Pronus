package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.ClienteEntity;
import com.inatel.prototipo_ia.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    // Criar cliente
    public ClienteEntity criar(ClienteEntity cliente) {
        return repository.save(cliente);
    }

    // Buscar todos os clientes
    public List<ClienteEntity> buscarTodos() {
        return repository.findAll();
    }

    // Buscar cliente por ID
    public Optional<ClienteEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Buscar clientes maiores de 18 anos
    public List<ClienteEntity> buscarMaioresDeIdade() {
        return repository.findClientesMaioresDeIdade();
    }

    // Buscar clientes por nível
    public List<ClienteEntity> buscarPorNivel(String nivel) {
        return repository.findByNivel(nivel);
    }

    // Buscar clientes por nível e idade mínima
    public List<ClienteEntity> buscarPorNivelEIdadeMinima(String nivel, Integer idade) {
        return repository.findByNivelAndIdadeMinima(nivel, idade);
    }

    // Atualizar cliente
    public ClienteEntity atualizar(ClienteEntity cliente) {
        return repository.save(cliente);
    }

    // Deletar cliente
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
