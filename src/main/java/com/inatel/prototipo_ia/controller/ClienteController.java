package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.entity.ClienteEntity;
import com.inatel.prototipo_ia.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    // Criar cliente
    @PostMapping
    public ResponseEntity<ClienteEntity> criar(@RequestBody ClienteEntity cliente) {
        ClienteEntity clienteCriado = service.criar(cliente);
        return ResponseEntity.ok(clienteCriado);
    }

    // Buscar todos os clientes
    @GetMapping
    public ResponseEntity<List<ClienteEntity>> buscarTodos() {
        List<ClienteEntity> clientes = service.buscarTodos();
        return ResponseEntity.ok(clientes);
    }

    // Buscar cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> buscarPorId(@PathVariable Long id) {
        Optional<ClienteEntity> cliente = service.buscarPorId(id);
        return cliente.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // Buscar clientes maiores de 18 anos
    @GetMapping("/maiores-idade")
    public ResponseEntity<List<ClienteEntity>> buscarMaioresDeIdade() {
        List<ClienteEntity> clientes = service.buscarMaioresDeIdade();
        return ResponseEntity.ok(clientes);
    }

    // Buscar clientes por nível
    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<ClienteEntity>> buscarPorNivel(@PathVariable String nivel) {
        List<ClienteEntity> clientes = service.buscarPorNivel(nivel);
        return ResponseEntity.ok(clientes);
    }



    // Buscar clientes por nível e idade mínima
    @GetMapping("/nivel/{nivel}/idade-minima/{idade}")
    public ResponseEntity<List<ClienteEntity>> buscarPorNivelEIdadeMinima(
            @PathVariable String nivel, 
            @PathVariable Integer idade) {
        List<ClienteEntity> clientes = service.buscarPorNivelEIdadeMinima(nivel, idade);
        return ResponseEntity.ok(clientes);
    }

    // Atualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> atualizar(@PathVariable Long id, @RequestBody ClienteEntity cliente) {
        cliente.setId(id);
        ClienteEntity clienteAtualizado = service.atualizar(cliente);
        return ResponseEntity.ok(clienteAtualizado);
    }

    // Deletar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}