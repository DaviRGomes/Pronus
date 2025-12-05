package com.inatel.prototipo_ia.dto.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessaoTreinoDtoIn {
    private Long clienteId;
    private Long especialistaId;
    private String dificuldade; // R, L, S, CH, LH, etc.
    private Integer idade; // Para gerar palavras adequadas
}