package com.inatel.prototipo_ia.repository;

import com.inatel.prototipo_ia.entity.SessaoTreinoEntity;
import com.inatel.prototipo_ia.entity.SessaoTreinoEntity.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoTreinoRepository extends JpaRepository<SessaoTreinoEntity, Long> {

    // Buscar sessões de um cliente
    List<SessaoTreinoEntity> findByClienteId(Long clienteId);

    // Buscar sessões de um especialista
    List<SessaoTreinoEntity> findByEspecialistaId(Long especialistaId);

    // Buscar sessão ativa de um cliente (não finalizada e não cancelada)
    @Query("SELECT s FROM SessaoTreinoEntity s WHERE s.cliente.id = :clienteId AND s.status NOT IN ('FINALIZADA', 'CANCELADA') ORDER BY s.dataInicio DESC")
    List<SessaoTreinoEntity> findSessoesAtivasByClienteId(Long clienteId);

    // Buscar sessão por status
    List<SessaoTreinoEntity> findByStatus(StatusSessao status);

    // Buscar sessões finalizadas de um cliente
    List<SessaoTreinoEntity> findByClienteIdAndStatus(Long clienteId, StatusSessao status);

    // Buscar última sessão de um cliente
    Optional<SessaoTreinoEntity> findTopByClienteIdOrderByDataInicioDesc(Long clienteId);

    // Verificar se cliente tem sessão em andamento
    boolean existsByClienteIdAndStatusIn(Long clienteId, List<StatusSessao> statuses);
}