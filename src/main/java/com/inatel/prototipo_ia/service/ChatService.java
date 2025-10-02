package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.ChatEntity;
import com.inatel.prototipo_ia.entity.ClienteEntity;
import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import com.inatel.prototipo_ia.repository.ChatRepository;
import com.inatel.prototipo_ia.repository.ClienteRepository;
import com.inatel.prototipo_ia.repository.ProfissionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final ClienteRepository clienteRepository;
    private final ProfissionalRepository profissionalRepository;

    public ChatService(ChatRepository chatRepository, ClienteRepository clienteRepository, ProfissionalRepository profissionalRepository) {
        this.chatRepository = chatRepository;
        this.clienteRepository = clienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    /**
     * Cria um novo chat após validar os participantes.
     */
    public ChatEntity criar(ChatEntity chat) {
        // validacao da estrutura
        validarDadosDoChat(chat);

        // Validacao da integridade do cliente
        Optional<ClienteEntity> optionalCliente = clienteRepository.findById(chat.getCliente().getId());
        if (optionalCliente.isEmpty()) {
            throw new EntityNotFoundException("Cliente não encontrado com o ID: " + chat.getCliente().getId());
        }
        ClienteEntity cliente = optionalCliente.get();

        // validacao da integridade do profissional
        Optional<ProfissionalEntity> optionalProfissional = profissionalRepository.findById(chat.getProfissional().getId());
        if (optionalProfissional.isEmpty()) {
            throw new EntityNotFoundException("Profissional não encontrado com o ID: " + chat.getProfissional().getId());
        }
        ProfissionalEntity profissional = optionalProfissional.get();
        
        chat.setCliente(cliente);
        chat.setProfissional(profissional);

        return chatRepository.save(chat);
    }

    /**
     * Atualiza um chat existente.
     */
    public ChatEntity atualizar(Long id, ChatEntity chatAtualizado) {

        Optional<ChatEntity> optionalChat = chatRepository.findById(id);

        if (optionalChat.isEmpty()) {
            throw new EntityNotFoundException("Chat não encontrado com o ID: " + id);
        }

        ChatEntity chatExistente = optionalChat.get();

        chatExistente.setDuracao(chatAtualizado.getDuracao());
        chatExistente.setConversa(chatAtualizado.getConversa());

        return chatRepository.save(chatExistente);
    }

    public ChatEntity atualizar(ChatEntity chatAtualizado) {
        if (chatAtualizado == null || chatAtualizado.getId() == null) {
            throw new IllegalArgumentException("O chat para atualização deve ter um ID.");
        }
        return atualizar(chatAtualizado.getId(), chatAtualizado);
    }

    /**
     * Deleta um chat pelo seu ID.
     */
    public void deletar(Long id) {
        
        if (!chatRepository.existsById(id)) {
            throw new EntityNotFoundException("Chat não encontrado com o ID: " + id);
        }
        
        chatRepository.deleteById(id);
    }

    /**
     * Busca todos os chats cadastrados.
     */
    public List<ChatEntity> buscarTodos() {
        return chatRepository.findAll();
    }

    /**
     * Busca um chat específico pelo seu ID.
     */
    public Optional<ChatEntity> buscarPorId(Long id) {
        return chatRepository.findById(id);
    }

    /**
     * Busca todos os chats de um cliente específico.
     */
    public List<ChatEntity> buscarPorClienteId(Long clienteId) {
        return chatRepository.findByClienteId(clienteId);
    }

    /**
     * Busca todos os chats de um profissional específico.
     */
    public List<ChatEntity> buscarPorProfissionalId(Long profissionalId) {
        return chatRepository.findByProfissionalId(profissionalId);
    }

    /**
     * Busca chats com duração maior que um valor especificado.
     */
    public List<ChatEntity> buscarComDuracaoMaiorQue(Integer minutos) {
        if (minutos == null || minutos < 0) {
            throw new IllegalArgumentException("A duração em minutos deve ser um número positivo.");
        }
        return chatRepository.findByDuracaoGreaterThan(minutos);
    }

    public List<ChatEntity> buscarChatsLongos() {
        return chatRepository.findChatsLongos();
    }

    private void validarDadosDoChat(ChatEntity chat) {
        if (chat == null) {
            throw new IllegalArgumentException("O objeto de chat não pode ser nulo.");
        }
        if (chat.getCliente() == null || chat.getCliente().getId() == null) {
            throw new IllegalArgumentException("O cliente associado ao chat é obrigatório.");
        }
        if (chat.getProfissional() == null || chat.getProfissional().getId() == null) {
            throw new IllegalArgumentException("O profissional associado ao chat é obrigatório.");
        }
    }
}