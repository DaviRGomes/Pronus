package com.inatel.prototipo_ia.repository;

import com.inatel.prototipo_ia.entity.TratamentoEntity;
import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TratamentoRepository extends JpaRepository<TratamentoEntity, Long> {

    // Buscar tratamentos por profissional
    List<TratamentoEntity> findByProfissional(ProfissionalEntity profissional);
    
    // Buscar tratamentos por tipo
    List<TratamentoEntity> findByTipoTratamento(String tipoTratamento);
    
    // Buscar tratamentos com quantidade por dia maior que X
    List<TratamentoEntity> findByQuantidadeDiaGreaterThan(Integer quantidade);
    
    // Buscar tratamentos intensivos (mais de 3 por dia)
    @Query("SELECT t FROM TratamentoEntity t WHERE t.quantidadeDia > 3")
    List<TratamentoEntity> findTratamentosIntensivos();
    
    // Buscar tratamentos por ID do profissional
    List<TratamentoEntity> findByProfissionalId(Long profissionalId);
    
    // Buscar tratamentos por tipo e quantidade mínima
    List<TratamentoEntity> findByTipoTratamentoAndQuantidadeDiaGreaterThanEqual(String tipo, Integer quantidade);
    
    // Buscar tratamentos por tipo (ignorando case)
    List<TratamentoEntity> findByTipoTratamentoIgnoreCase(String tipoTratamento);

    // Verifica se existe um tratamento associado a um ID de profissional.
    boolean existsByProfissionalId(Long profissionalId);
}