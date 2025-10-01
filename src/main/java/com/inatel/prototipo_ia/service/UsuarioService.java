package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.UsuarioEntity;
import com.inatel.prototipo_ia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // Criar usuário
    public UsuarioEntity criar(UsuarioEntity usuario) {
        return repository.save(usuario);
    }

    // Buscar todos os usuários
    public List<UsuarioEntity> buscarTodos() {
        return repository.findAll();
    }

    // Buscar usuário por ID
    public Optional<UsuarioEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Método de busca por nome temporariamente removido

    // Buscar usuários por idade
    public List<UsuarioEntity> buscarPorIdade(Integer idade) {
        return repository.findByIdade(idade);
    }

    // Atualizar usuário
    public UsuarioEntity atualizar(UsuarioEntity usuario) {
        return repository.save(usuario);
    }

    // Deletar usuário
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
