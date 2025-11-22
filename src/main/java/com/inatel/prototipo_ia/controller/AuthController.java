package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.dto.in.LoginDtoIn;
import com.inatel.prototipo_ia.dto.out.TokenDtoOut;
import com.inatel.prototipo_ia.entity.UsuarioEntity;
import com.inatel.prototipo_ia.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenDtoOut> efetuarLogin(@RequestBody @Valid LoginDtoIn dados) {
        // 1. Cria o token de autenticação com os dados recebidos
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());

        // 2. O Spring Security verifica no banco se o login/senha batem (usando o AutenticacaoService)
        var authentication = manager.authenticate(authenticationToken);

        // 3. Se chegou aqui, deu certo! Pegamos o usuário logado
        var usuario = (UsuarioEntity) authentication.getPrincipal();
        
        // 4. Geramos o Token JWT para ele
        var tokenJWT = tokenService.gerarToken(usuario);

        // 5. Montamos o DTO de retorno (Token + ID + Nome)
        TokenDtoOut tokenDto = new TokenDtoOut();
        tokenDto.setToken(tokenJWT);
        tokenDto.setId(usuario.getId());
        tokenDto.setNome(usuario.getNome());
        tokenDto.setTipoUsuario("CLIENTE"); // Simplificação: assumimos Cliente por enquanto

        return ResponseEntity.ok(tokenDto);
    }
}