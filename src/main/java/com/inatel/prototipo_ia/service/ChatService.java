package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.ChatEntity;
import com.inatel.prototipo_ia.entity.ClienteEntity;
import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import com.inatel.prototipo_ia.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository repository;

    public ChatService(ChatRepository repository) {
        this.repository = repository;
    }

    // Criar chat
    public ChatEntity criar(ChatEntity chat) {
        return repository.save(chat);
    }

    // Buscar todos os chats
    public List<ChatEntity> buscarTodos() {
        return repository.findAll();
    }

    // Buscar chat por ID
    public Optional<ChatEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Buscar chats por cliente
    public List<ChatEntity> buscarPorCliente(ClienteEntity cliente) {
        return repository.findByCliente(cliente);
    }

    // Buscar chats por profissional
    public List<ChatEntity> buscarPorProfissional(ProfissionalEntity profissional) {
        return repository.findByProfissional(profissional);
    }

    // Buscar chats por ID do cliente
    public List<ChatEntity> buscarPorClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    // Buscar chats por ID do profissional
    public List<ChatEntity> buscarPorProfissionalId(Long profissionalId) {
        return repository.findByProfissionalId(profissionalId);
    }

    // Buscar chats longos (mais de 30 minutos)
    public List<ChatEntity> buscarChatsLongos() {
        return repository.findChatsLongos();
    }

    // Buscar chats com duração maior que X minutos
    public List<ChatEntity> buscarComDuracaoMaiorQue(Integer minutos) {
        return repository.findByDuracaoGreaterThan(minutos);
    }


    // Atualizar chat
    public ChatEntity atualizar(ChatEntity chat) {
        return repository.save(chat);
    }

    // Deletar chat
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}