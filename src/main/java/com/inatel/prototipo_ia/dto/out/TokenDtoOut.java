package com.inatel.prototipo_ia.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDtoOut {
    private String token;
    private Long id; // ID do usuário logado (Cliente/Especialista)
    private String tipoUsuario; // Ex: CLIENTE, ESPECIALISTA, SECRETARIA
    private String nome; // Nome do usuário
}