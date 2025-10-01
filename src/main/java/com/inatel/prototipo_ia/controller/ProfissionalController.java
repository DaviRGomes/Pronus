package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.entity.ProfissionalEntity;
import com.inatel.prototipo_ia.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    @Autowired
    private ProfissionalService service;

    // Criar profissional
    @PostMapping
    public ResponseEntity<ProfissionalEntity> criar(@RequestBody ProfissionalEntity profissional) {
        ProfissionalEntity profissionalCriado = service.criar(profissional);
        return ResponseEntity.ok(profissionalCriado);
    }

    // Buscar todos os profissionais
    @GetMapping
    public ResponseEntity<List<ProfissionalEntity>> buscarTodos() {
        List<ProfissionalEntity> profissionais = service.buscarTodos();
        return ResponseEntity.ok(profissionais);
    }

    // Buscar profissional por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalEntity> buscarPorId(@PathVariable Long id) {
        Optional<ProfissionalEntity> profissional = service.buscarPorId(id);
        return profissional.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    // Buscar profissionais experientes (mais de 5 anos)
    @GetMapping("/experientes")
    public ResponseEntity<List<ProfissionalEntity>> buscarExperientes() {
        List<ProfissionalEntity> profissionais = service.buscarExperientes();
        return ResponseEntity.ok(profissionais);
    }

    // Buscar profissionais com experiência maior que X anos
    @GetMapping("/experiencia-maior/{anos}")
    public ResponseEntity<List<ProfissionalEntity>> buscarComExperienciaMaiorQue(@PathVariable Integer anos) {
        List<ProfissionalEntity> profissionais = service.buscarComExperienciaMaiorQue(anos);
        return ResponseEntity.ok(profissionais);
    }

    // Buscar profissionais por certificado
    @GetMapping("/certificado/{certificado}")
    public ResponseEntity<List<ProfissionalEntity>> buscarPorCertificado(@PathVariable String certificado) {
        List<ProfissionalEntity> profissionais = service.buscarPorCertificado(certificado);
        return ResponseEntity.ok(profissionais);
    }



    // Buscar profissionais qualificados
    @GetMapping("/qualificados/{experienciaMinima}/{idadeMinima}")
    public ResponseEntity<List<ProfissionalEntity>> buscarQualificados(
            @PathVariable Integer experienciaMinima, 
            @PathVariable Integer idadeMinima) {
        List<ProfissionalEntity> profissionais = service.buscarQualificados(experienciaMinima, idadeMinima);
        return ResponseEntity.ok(profissionais);
    }

    // Atualizar profissional
    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalEntity> atualizar(@PathVariable Long id, @RequestBody ProfissionalEntity profissional) {
        profissional.setId(id);
        ProfissionalEntity profissionalAtualizado = service.atualizar(profissional);
        return ResponseEntity.ok(profissionalAtualizado);
    }

    // Deletar profissional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}