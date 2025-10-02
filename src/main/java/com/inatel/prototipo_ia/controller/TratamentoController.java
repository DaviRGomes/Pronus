package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.entity.TratamentoEntity;
import com.inatel.prototipo_ia.service.TratamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tratamentos")
public class TratamentoController {

    @Autowired
    private TratamentoService service;

    // Criar tratamento
    @PostMapping
    public ResponseEntity<TratamentoEntity> criar(@RequestBody TratamentoEntity tratamento) {
        TratamentoEntity tratamentoCriado = service.criar(tratamento);
        return ResponseEntity.ok(tratamentoCriado);
    }

    // Buscar todos os tratamentos
    @GetMapping
    public ResponseEntity<List<TratamentoEntity>> buscarTodos() {
        List<TratamentoEntity> tratamentos = service.buscarTodos();
        return ResponseEntity.ok(tratamentos);
    }

    // Buscar tratamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<TratamentoEntity> buscarPorId(@PathVariable Long id) {
        Optional<TratamentoEntity> tratamento = service.buscarPorId(id);
        return tratamento.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // Buscar tratamentos por ID do profissional
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<TratamentoEntity>> buscarPorProfissionalId(@PathVariable Long profissionalId) {
        List<TratamentoEntity> tratamentos = service.buscarPorProfissionalId(profissionalId);
        return ResponseEntity.ok(tratamentos);
    }

    // Buscar tratamentos por tipo
    @GetMapping("/tipo/{tipoTratamento}")
    public ResponseEntity<List<TratamentoEntity>> buscarPorTipo(@PathVariable String tipoTratamento) {
        List<TratamentoEntity> tratamentos = service.buscarPorTipo(tipoTratamento);
        return ResponseEntity.ok(tratamentos);
    }

    // // Buscar tratamentos intensivos (mais de 3 por dia)
    // @GetMapping("/intensivos")
    // public ResponseEntity<List<TratamentoEntity>> buscarIntensivos() {
    //     List<TratamentoEntity> tratamentos = service.buscarIntensivos();
    //     return ResponseEntity.ok(tratamentos);
    // }

    // // Buscar tratamentos com quantidade por dia maior que X
    // @GetMapping("/quantidade-maior/{quantidade}")
    // public ResponseEntity<List<TratamentoEntity>> buscarComQuantidadeMaiorQue(@PathVariable Integer quantidade) {
    //     List<TratamentoEntity> tratamentos = service.buscarComQuantidadeMaiorQue(quantidade);
    //     return ResponseEntity.ok(tratamentos);
    // }

    // Buscar tratamentos por tipo e quantidade mínima
    @GetMapping("/tipo/{tipo}/quantidade-minima/{quantidade}")
    public ResponseEntity<List<TratamentoEntity>> buscarPorTipoEQuantidadeMinima(
            @PathVariable String tipo, 
            @PathVariable Integer quantidade) {
        List<TratamentoEntity> tratamentos = service.buscarPorTipoEQuantidadeMinima(tipo, quantidade);
        return ResponseEntity.ok(tratamentos);
    }

    // Atualizar tratamento
    @PutMapping("/{id}")
    public ResponseEntity<TratamentoEntity> atualizar(@PathVariable Long id, @RequestBody TratamentoEntity tratamento) {
        tratamento.setId(id);
        TratamentoEntity tratamentoAtualizado = service.atualizar(tratamento);
        return ResponseEntity.ok(tratamentoAtualizado);
    }

    // Deletar tratamento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}