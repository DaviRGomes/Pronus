package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.entity.ChatEntity;
import com.inatel.prototipo_ia.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService service;

    // Criar chat
    @PostMapping
    public ResponseEntity<ChatEntity> criar(@RequestBody ChatEntity chat) {
        ChatEntity chatCriado = service.criar(chat);
        return ResponseEntity.ok(chatCriado);
    }

    // Buscar todos os chats
    @GetMapping
    public ResponseEntity<List<ChatEntity>> buscarTodos() {
        List<ChatEntity> chats = service.buscarTodos();
        return ResponseEntity.ok(chats);
    }

    // Buscar chat por ID
    @GetMapping("/{id}")
    public ResponseEntity<ChatEntity> buscarPorId(@PathVariable Long id) {
        Optional<ChatEntity> chat = service.buscarPorId(id);
        return chat.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    // Buscar chats por ID do cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ChatEntity>> buscarPorClienteId(@PathVariable Long clienteId) {
        List<ChatEntity> chats = service.buscarPorClienteId(clienteId);
        return ResponseEntity.ok(chats);
    }

    // Buscar chats por ID do profissional
    @GetMapping("/profissional/{profissionalId}")
    public ResponseEntity<List<ChatEntity>> buscarPorProfissionalId(@PathVariable Long profissionalId) {
        List<ChatEntity> chats = service.buscarPorProfissionalId(profissionalId);
        return ResponseEntity.ok(chats);
    }

    // Buscar chats longos (mais de 30 minutos)
    @GetMapping("/longos")
    public ResponseEntity<List<ChatEntity>> buscarChatsLongos() {
        List<ChatEntity> chats = service.buscarChatsLongos();
        return ResponseEntity.ok(chats);
    }

    // Buscar chats com duração maior que X minutos
    @GetMapping("/duracao-maior/{minutos}")
    public ResponseEntity<List<ChatEntity>> buscarComDuracaoMaiorQue(@PathVariable Integer minutos) {
        List<ChatEntity> chats = service.buscarComDuracaoMaiorQue(minutos);
        return ResponseEntity.ok(chats);
    }

    // Buscar chats por palavra na conversa
    @GetMapping("/conversa/{palavra}")
    public ResponseEntity<List<ChatEntity>> buscarPorPalavraConversa(@PathVariable String palavra) {
        List<ChatEntity> chats = service.buscarPorPalavraConversa(palavra);
        return ResponseEntity.ok(chats);
    }

    // Atualizar chat
    @PutMapping("/{id}")
    public ResponseEntity<ChatEntity> atualizar(@PathVariable Long id, @RequestBody ChatEntity chat) {
        chat.setId(id);
        ChatEntity chatAtualizado = service.atualizar(chat);
        return ResponseEntity.ok(chatAtualizado);
    }

    // Deletar chat
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}