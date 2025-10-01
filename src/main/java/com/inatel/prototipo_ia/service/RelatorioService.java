package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.RelatorioEntity;
import com.inatel.prototipo_ia.entity.ChatEntity;
import com.inatel.prototipo_ia.repository.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelatorioService {

    private final RelatorioRepository repository;

    public RelatorioService(RelatorioRepository repository) {
        this.repository = repository;
    }

    // Criar relatório
    public RelatorioEntity criar(RelatorioEntity relatorio) {
        return repository.save(relatorio);
    }

    // Buscar todos os relatórios
    public List<RelatorioEntity> buscarTodos() {
        return repository.findAll();
    }

    // Buscar relatório por ID
    public Optional<RelatorioEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Buscar relatório por chat
    public Optional<RelatorioEntity> buscarPorChat(ChatEntity chat) {
        return repository.findByChat(chat);
    }

    // Buscar relatório por ID do chat
    public Optional<RelatorioEntity> buscarPorChatId(Long chatId) {
        return repository.findByChatId(chatId);
    }

    // Métodos de busca específicos foram temporariamente removidos para resolver problemas de inicialização

    // Atualizar relatório
    public RelatorioEntity atualizar(RelatorioEntity relatorio) {
        return repository.save(relatorio);
    }

    // Deletar relatório
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}