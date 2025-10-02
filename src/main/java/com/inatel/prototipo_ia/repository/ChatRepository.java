package com.inatel.prototipo_ia.repository;

import com.inatel.prototipo_ia.entity.ChatEntity;
import com.inatel.prototipo_ia.entity.ClienteEntity;
import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    // Buscar chats por cliente
    List<ChatEntity> findByCliente(ClienteEntity cliente);
    
    // Buscar chats por profissional
    List<ChatEntity> findByProfissional(ProfissionalEntity profissional);
    
    // Buscar chats com duração maior que X minutos
    List<ChatEntity> findByDuracaoGreaterThan(Integer minutos);
    
    // Buscar chats longos (mais de 30 minutos)
    @Query("SELECT c FROM ChatEntity c WHERE c.duracao > 30")
    List<ChatEntity> findChatsLongos();
    
    // Buscar chats por ID do cliente
    List<ChatEntity> findByClienteId(Long clienteId);
    
    // Buscar chats por ID do profissional
    List<ChatEntity> findByProfissionalId(Long profissionalId);
    
    // Verifica se existe um chat associado a um ID de cliente.
    boolean existsByClienteId(Long clienteId);

    // Verifica se existe um chat associado a um ID de profissional.
    boolean existsByProfissionalId(Long profissionalId);
}