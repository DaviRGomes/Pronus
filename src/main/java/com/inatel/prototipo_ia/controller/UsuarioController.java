package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.entity.UsuarioEntity;
import com.inatel.prototipo_ia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Criar usuário
    @PostMapping
    public ResponseEntity<UsuarioEntity> criar(@RequestBody UsuarioEntity usuario) {
        UsuarioEntity usuarioCriado = service.criar(usuario);
        return ResponseEntity.ok(usuarioCriado);
    }

    // Buscar todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> buscarTodos() {
        List<UsuarioEntity> usuarios = service.buscarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> buscarPorId(@PathVariable Long id) {
        Optional<UsuarioEntity> usuario = service.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }


    // Buscar usuários por idade
    @GetMapping("/idade/{idade}")
    public ResponseEntity<List<UsuarioEntity>> buscarPorIdade(@PathVariable Integer idade) {
        List<UsuarioEntity> usuarios = service.buscarPorIdade(idade);
        return ResponseEntity.ok(usuarios);
    }

    // Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> atualizar(@PathVariable Long id, @RequestBody UsuarioEntity usuario) {
        usuario.setId(id);
        UsuarioEntity usuarioAtualizado = service.atualizar(usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    // Deletar usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
    // Removidos os endpoints:
    // - /maiores-idade
    // - /nome/{nome}
    // - /endereco/{endereco}
    // - /idade-entre/{idadeMin}/{idadeMax}
    // Mantidos os que já existem na Service:
}