package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.ClienteEntity;
import com.inatel.prototipo_ia.repository.ChatRepository;
import com.inatel.prototipo_ia.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ChatRepository chatRepository;

    public ClienteService(ClienteRepository clienteRepository, ChatRepository chatRepository) {
        this.clienteRepository = clienteRepository;
        this.chatRepository = chatRepository;
    }

    /**
     * Cria um novo cliente (e o usuário base correspondente).
     */
    public ClienteEntity criar(ClienteEntity cliente) {
        validarCliente(cliente);

        if (cliente.getId() != null && clienteRepository.existsById(cliente.getId())) {
            throw new IllegalStateException("Já existe um cliente com o ID: " + cliente.getId());
        }
        
        return clienteRepository.save(cliente);
    }

    /**
     * Busca todos os clientes.
     */
    public List<ClienteEntity> buscarTodos() {
        return clienteRepository.findAll();
    }

    /**
     * Busca um cliente pelo seu ID (que é o mesmo ID do usuário).
     */
    public Optional<ClienteEntity> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Atualiza os dados de um cliente/usuário.
     */
    public ClienteEntity atualizar(Long id, ClienteEntity clienteAtualizado) {

        Optional<ClienteEntity> optionalCliente = clienteRepository.findById(id);
        if (optionalCliente.isEmpty()) {
            throw new EntityNotFoundException("Cliente não encontrado com o ID: " + id);
        }

        ClienteEntity clienteExistente = optionalCliente.get();
        
        validarCliente(clienteAtualizado);

        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setIdade(clienteAtualizado.getIdade());
        clienteExistente.setEndereco(clienteAtualizado.getEndereco());
        clienteExistente.setNivel(clienteAtualizado.getNivel());

        return clienteRepository.save(clienteExistente);
    }

    /**
     * Deleta um cliente, se ele não estiver participando de nenhum chat.
     */
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new EntityNotFoundException("Cliente não encontrado com o ID: " + id);
        }

        if (chatRepository.existsByClienteId(id)) {
            throw new IllegalStateException("Não é possível deletar o cliente pois ele está associado a um ou mais chats.");
        }

        clienteRepository.deleteById(id);
    }

    /**
     * Valida os campos obrigatórios e herdados do usuário.
     */
    private void validarCliente(ClienteEntity cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("O objeto de cliente não pode ser nulo.");
        }

        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do cliente/usuário é obrigatório.");
        }
    }

    public ClienteEntity atualizar(ClienteEntity clienteAtualizado) {
        if (clienteAtualizado == null || clienteAtualizado.getId() == null) {
            throw new IllegalArgumentException("O cliente para atualização deve ter um ID.");
        }
        return atualizar(clienteAtualizado.getId(), clienteAtualizado);
    }

    public List<ClienteEntity> buscarMaioresDeIdade() {
        return clienteRepository.findClientesMaioresDeIdade();
    }

    public List<ClienteEntity> buscarPorNivel(String nivel) {
        if (nivel == null || nivel.isBlank()) {
            throw new IllegalArgumentException("O nível não pode ser vazio.");
        }
        return clienteRepository.findByNivel(nivel);
    }

    public List<ClienteEntity> buscarPorNivelEIdadeMinima(String nivel, Integer idade) {
        if (nivel == null || nivel.isBlank()) {
            throw new IllegalArgumentException("O nível não pode ser vazio.");
        }
        if (idade == null || idade < 0) {
            throw new IllegalArgumentException("A idade mínima deve ser não negativa.");
        }
        return clienteRepository.findByNivelAndIdadeMinima(nivel, idade);
    }
}
