package com.inatel.prototipo_ia.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessaoTreinoDtoOut {

    private Long id;
    private Long clienteId;
    private String clienteNome;
    private Long especialistaId;
    private String especialistaNome;

    // Configuração
    private String dificuldade;
    private Integer idadeCliente;
    private Integer totalCiclos;
    private Integer palavrasPorCiclo;

    // Estado
    private Integer cicloAtual;
    private String status;

    // Palavras
    private List<String> palavrasCiclo1;
    private List<String> palavrasCiclo2;
    private List<String> palavrasCiclo3;

    // Resultados
    private MensagemSessaoDtoOut.ResultadoCiclo resultadoCiclo1;
    private MensagemSessaoDtoOut.ResultadoCiclo resultadoCiclo2;
    private MensagemSessaoDtoOut.ResultadoCiclo resultadoCiclo3;

    // Métricas
    private Integer totalPalavras;
    private Integer totalAcertos;
    private Double pontuacaoGeral;
    private Double porcentagemAcerto;

    // Timestamps
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer duracaoMinutos;

    // Histórico
    private String historicoConversa;
}