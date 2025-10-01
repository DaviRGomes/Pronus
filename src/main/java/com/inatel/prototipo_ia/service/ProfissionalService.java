package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import com.inatel.prototipo_ia.repository.ProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalService {

    private final ProfissionalRepository repository;

    public ProfissionalService(ProfissionalRepository repository) {
        this.repository = repository;
    }

    // Criar profissional
    public ProfissionalEntity criar(ProfissionalEntity profissional) {
        return repository.save(profissional);
    }

    // Buscar todos os profissionais
    public List<ProfissionalEntity> buscarTodos() {
        return repository.findAll();
    }

    // Buscar profissional por ID
    public Optional<ProfissionalEntity> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // Buscar profissionais experientes (mais de 5 anos)
    public List<ProfissionalEntity> buscarExperientes() {
        return repository.findProfissionaisExperientes();
    }

    // Buscar profissionais com experiência maior que X anos
    public List<ProfissionalEntity> buscarComExperienciaMaiorQue(Integer anos) {
        return repository.findByExperienciaGreaterThan(anos);
    }

    // Método de busca por certificado temporariamente removido

    // Buscar profissionais qualificados (experiência e idade mínimas)
    public List<ProfissionalEntity> buscarQualificados(Integer experienciaMinima, Integer idadeMinima) {
        return repository.findByExperienciaAndIdadeMinima(experienciaMinima, idadeMinima);
    }

    // Atualizar profissional
    public ProfissionalEntity atualizar(ProfissionalEntity profissional) {
        return repository.save(profissional);
    }

    // Deletar profissional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}