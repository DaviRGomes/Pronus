package com.inatel.prototipo_ia.repository;

import com.inatel.prototipo_ia.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    List<ChatEntity> findByClienteId(Long clienteId);

    // --- ALTERADO PARA ESPECIALISTA ---
    List<ChatEntity> findByEspecialistaId(Long especialistaId);

    boolean existsByClienteId(Long clienteId);
    
    // Query customizada (mantida, mas ajustada se necessário)
    @Query("SELECT c FROM ChatEntity c WHERE c.duracao > 30")
    List<ChatEntity> findChatsLongos();
}
