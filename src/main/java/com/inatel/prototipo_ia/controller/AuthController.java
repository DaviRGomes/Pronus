package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.dto.in.LoginDtoIn;
import com.inatel.prototipo_ia.dto.out.TokenDtoOut;
import com.inatel.prototipo_ia.entity.UsuarioEntity;
import com.inatel.prototipo_ia.repository.ClienteRepository;    
import com.inatel.prototipo_ia.repository.EspecialistaRepository; 
import com.inatel.prototipo_ia.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.inatel.prototipo_ia.repository.SecretariaRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;
    
    // Injetamos os repositórios para checar o tipo
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private EspecialistaRepository especialistaRepository;

    @Autowired 
    private SecretariaRepository secretariaRepository;

    @PostMapping("/login")
    public ResponseEntity<TokenDtoOut> efetuarLogin(@RequestBody @Valid LoginDtoIn dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
        var authentication = manager.authenticate(authenticationToken);
        var usuario = (UsuarioEntity) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuario);

        TokenDtoOut tokenDto = new TokenDtoOut();
        tokenDto.setToken(tokenJWT);
        tokenDto.setId(usuario.getId());
        tokenDto.setNome(usuario.getNome());

        // --- LÓGICA DE DESCOBERTA DE TIPO ---
        if (clienteRepository.existsById(usuario.getId())) {
            tokenDto.setTipoUsuario("CLIENTE");
        } else if (especialistaRepository.existsById(usuario.getId())) {
            tokenDto.setTipoUsuario("ESPECIALISTA");
        } else if (secretariaRepository.existsById(usuario.getId())) {
            tokenDto.setTipoUsuario("SECRETARIA");
        } else {
            tokenDto.setTipoUsuario("ADMIN"); // Fallback
        }
        // ------------------------------------

        return ResponseEntity.ok(tokenDto);
    }
}