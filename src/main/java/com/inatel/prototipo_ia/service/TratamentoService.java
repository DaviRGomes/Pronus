package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.TratamentoEntity;
import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import com.inatel.prototipo_ia.repository.TratamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TratamentoService {

    private final TratamentoRepository repository;

    public TratamentoService(TratamentoRepository repository) {
        this.repository = repository;
    }

    

    // Criar tratamento
    public TratamentoEntity criar(TratamentoEntity tratamento) {
        return repository.save(tratamento);
    }

    // Buscar todos os tratamentos
    public List<TratamentoEntity> buscarTodos() {
        return repository.findAll();
    }

    // Buscar tratamento por ID
    public Optional<TratamentoEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Buscar tratamentos por profissional
    public List<TratamentoEntity> buscarPorProfissional(ProfissionalEntity profissional) {
        return repository.findByProfissional(profissional);
    }

    // Buscar tratamentos por ID do profissional
    public List<TratamentoEntity> buscarPorProfissionalId(Long profissionalId) {
        return repository.findByProfissionalId(profissionalId);
    }

    // Buscar tratamentos por tipo
    public List<TratamentoEntity> buscarPorTipo(String tipoTratamento) {
        return repository.findByTipoTratamento(tipoTratamento);
    }

    // Buscar tratamentos intensivos (mais de 3 por dia)
    public List<TratamentoEntity> buscarIntensivos() {
        return repository.findTratamentosIntensivos();
    }

    // Buscar tratamentos com quantidade por dia maior que X
    public List<TratamentoEntity> buscarComQuantidadeMaiorQue(Integer quantidade) {
        return repository.findByQuantidadeDiaGreaterThan(quantidade);
    }

    // Buscar tratamentos por tipo e quantidade mínima
    public List<TratamentoEntity> buscarPorTipoEQuantidadeMinima(String tipo, Integer quantidade) {
        return repository.findByTipoTratamentoAndQuantidadeDiaGreaterThanEqual(tipo, quantidade);
    }

    // Buscar tratamentos por tipo (ignorando case)
    public List<TratamentoEntity> buscarPorTipoIgnoreCase(String tipoTratamento) {
        return repository.findByTipoTratamentoIgnoreCase(tipoTratamento);
    }

    // Atualizar tratamento
    public TratamentoEntity atualizar(TratamentoEntity tratamento) {
        return repository.save(tratamento);
    }

    // Deletar tratamento
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}