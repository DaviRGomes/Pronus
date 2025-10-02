package com.inatel.prototipo_ia.service;

import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import com.inatel.prototipo_ia.repository.ChatRepository;
import com.inatel.prototipo_ia.repository.ProfissionalRepository;
import com.inatel.prototipo_ia.repository.TratamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final ChatRepository chatRepository;
    private final TratamentoRepository tratamentoRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository, ChatRepository chatRepository, TratamentoRepository tratamentoRepository) {
        this.profissionalRepository = profissionalRepository;
        this.chatRepository = chatRepository;
        this.tratamentoRepository = tratamentoRepository;
    }

    /**
     * Cria um novo perfil de profissional (e o usuário base correspondente).
     */
    public ProfissionalEntity criar(ProfissionalEntity profissional) {
        validarProfissional(profissional);

        if (profissional.getId() != null && profissionalRepository.existsById(profissional.getId())) {
            throw new IllegalStateException("Já existe um profissional com o ID de usuário: " + profissional.getId());
        }

        return profissionalRepository.save(profissional);
    }

    /**
     * Busca todos os profissionais cadastrados.
     */
    public List<ProfissionalEntity> buscarTodos() {
        return profissionalRepository.findAll();
    }

    /**
     * Busca um profissional pelo seu ID (que é o mesmo ID do usuário).
     */
    public Optional<ProfissionalEntity> buscarPorId(Long id) {
        return profissionalRepository.findById(id);
    }

    /**
     * Atualiza os dados de um profissional/usuário.
     */
    public ProfissionalEntity atualizar(Long id, ProfissionalEntity profissionalAtualizado) {
        Optional<ProfissionalEntity> optionalProfissional = profissionalRepository.findById(id);
        if (optionalProfissional.isEmpty()) {
            throw new EntityNotFoundException("Profissional não encontrado com o ID: " + id);
        }

        ProfissionalEntity profissionalExistente = optionalProfissional.get();
        validarProfissional(profissionalAtualizado);

        profissionalExistente.setNome(profissionalAtualizado.getNome());
        profissionalExistente.setIdade(profissionalAtualizado.getIdade());
        profissionalExistente.setEndereco(profissionalAtualizado.getEndereco());
        profissionalExistente.setCertificados(profissionalAtualizado.getCertificados());
        profissionalExistente.setExperiencia(profissionalAtualizado.getExperiencia());

        return profissionalRepository.save(profissionalExistente);
    }

    /**
     * Deleta um profissional, se ele não estiver associado a chats ou tratamentos.
     */
    public void deletar(Long id) {
        if (!profissionalRepository.existsById(id)) {
            throw new EntityNotFoundException("Profissional não encontrado com o ID: " + id);
        }

        boolean emUsoEmChat = chatRepository.existsByProfissionalId(id);
        boolean emUsoEmTratamento = tratamentoRepository.existsByProfissionalId(id);

        if (emUsoEmChat || emUsoEmTratamento) {
            throw new IllegalStateException("Não é possível deletar o profissional pois ele está associado a chats ou tratamentos existentes.");
        }

        profissionalRepository.deleteById(id);
    }

    /**
     * Valida os campos obrigatórios e herdados do usuário.
     */
    private void validarProfissional(ProfissionalEntity profissional) {
        if (profissional == null) {
            throw new IllegalArgumentException("O objeto de profissional não pode ser nulo.");
        }
        if (profissional.getNome() == null || profissional.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do profissional/usuário é obrigatório.");
        }
        if (profissional.getCertificados() == null || profissional.getCertificados().isBlank()) {
            throw new IllegalArgumentException("Os certificados são obrigatórios.");
        }
    }

    // Sobrecarga: atualizar aceitando só a entidade (compatível com o controller)
    public ProfissionalEntity atualizar(ProfissionalEntity profissionalAtualizado) {
        if (profissionalAtualizado == null || profissionalAtualizado.getId() == null) {
            throw new IllegalArgumentException("O profissional para atualização deve ter um ID.");
        }
        return atualizar(profissionalAtualizado.getId(), profissionalAtualizado);
    }

    // Profissionais experientes (>= 5 anos)
    public List<ProfissionalEntity> buscarExperientes() {
        return profissionalRepository.findProfissionaisExperientes();
    }

    // Profissionais com experiência maior que X anos
    public List<ProfissionalEntity> buscarComExperienciaMaiorQue(Integer anos) {
        if (anos == null || anos < 0) {
            throw new IllegalArgumentException("Os anos de experiência devem ser não negativos.");
        }
        return profissionalRepository.findByExperienciaGreaterThan(anos);
    }

    // Profissionais qualificados por experiência mínima e idade mínima
    public List<ProfissionalEntity> buscarQualificados(Integer experienciaMinima, Integer idadeMinima) {
        if (experienciaMinima == null || experienciaMinima < 0) {
            throw new IllegalArgumentException("A experiência mínima deve ser não negativa.");
        }
        if (idadeMinima == null || idadeMinima < 0) {
            throw new IllegalArgumentException("A idade mínima deve ser não negativa.");
        }
        return profissionalRepository.findByExperienciaAndIdadeMinima(experienciaMinima, idadeMinima);
    }
}