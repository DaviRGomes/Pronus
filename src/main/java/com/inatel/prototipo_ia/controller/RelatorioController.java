package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.entity.RelatorioEntity;
import com.inatel.prototipo_ia.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService service;

    // Criar relatório
    @PostMapping
    public ResponseEntity<RelatorioEntity> criar(@RequestBody RelatorioEntity relatorio) {
        RelatorioEntity relatorioCriado = service.criar(relatorio);
        return ResponseEntity.ok(relatorioCriado);
    }

    // Buscar todos os relatórios
    @GetMapping
    public ResponseEntity<List<RelatorioEntity>> buscarTodos() {
        List<RelatorioEntity> relatorios = service.buscarTodos();
        return ResponseEntity.ok(relatorios);
    }

    // Buscar relatório por ID
    @GetMapping("/{id}")
    public ResponseEntity<RelatorioEntity> buscarPorId(@PathVariable Long id) {
        Optional<RelatorioEntity> relatorio = service.buscarPorId(id);
        return relatorio.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // Buscar relatório por ID do chat
    @GetMapping("/chat/{chatId}")
    public ResponseEntity<RelatorioEntity> buscarPorChatId(@PathVariable Long chatId) {
        Optional<RelatorioEntity> relatorio = service.buscarPorChatId(chatId);
        return relatorio.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // Buscar relatórios com alta acurácia (maior que 0.8)
    @GetMapping("/alta-acuracia")
    public ResponseEntity<List<RelatorioEntity>> buscarAltaAcuracia() {
        List<RelatorioEntity> relatorios = service.buscarAltaAcuracia();
        return ResponseEntity.ok(relatorios);
    }

    // Buscar relatórios com acurácia maior que X
    @GetMapping("/acuracia-maior/{acuracia}")
    public ResponseEntity<List<RelatorioEntity>> buscarComAcuraciaMaiorQue(@PathVariable Float acuracia) {
        List<RelatorioEntity> relatorios = service.buscarComAcuraciaMaiorQue(acuracia);
        return ResponseEntity.ok(relatorios);
    }

    // Buscar relatórios por análise fonoaudiológica
    @GetMapping("/analise/{analise}")
    public ResponseEntity<List<RelatorioEntity>> buscarPorAnalise(@PathVariable String analise) {
        List<RelatorioEntity> relatorios = service.buscarPorAnalise(analise);
        return ResponseEntity.ok(relatorios);
    }

    // Buscar relatórios por faixa de acurácia
    @GetMapping("/acuracia-entre/{acuraciaMin}/{acuraciaMax}")
    public ResponseEntity<List<RelatorioEntity>> buscarPorFaixaAcuracia(
            @PathVariable Float acuraciaMin, 
            @PathVariable Float acuraciaMax) {
        List<RelatorioEntity> relatorios = service.buscarPorFaixaAcuracia(acuraciaMin, acuraciaMax);
        return ResponseEntity.ok(relatorios);
    }

    // Atualizar relatório
    @PutMapping("/{id}")
    public ResponseEntity<RelatorioEntity> atualizar(@PathVariable Long id, @RequestBody RelatorioEntity relatorio) {
        relatorio.setId(id);
        RelatorioEntity relatorioAtualizado = service.atualizar(relatorio);
        return ResponseEntity.ok(relatorioAtualizado);
    }

    // Deletar relatório
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}