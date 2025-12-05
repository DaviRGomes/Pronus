package com.inatel.prototipo_ia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessaotreino")
@Getter
@Setter
public class SessaoTreinoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne
    @JoinColumn(name = "especialista_id", nullable = false)
    private EspecialistaEntity especialista;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    // Configurações da sessão
    @Column(nullable = false)
    private String dificuldade;

    @Column(name = "idade_cliente", nullable = false)
    private Integer idadeCliente;

    @Column(name = "total_ciclos", nullable = false)
    private Integer totalCiclos;

    @Column(name = "palavras_por_ciclo", nullable = false)
    private Integer palavrasPorCiclo;

    // Estado atual da sessão
    @Column(name = "ciclo_atual", nullable = false)
    private Integer cicloAtual;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSessao status;

    // Palavras de cada ciclo (JSON serializado)
    @Column(name = "palavras_ciclo1", columnDefinition = "TEXT")
    private String palavrasCiclo1;

    @Column(name = "palavras_ciclo2", columnDefinition = "TEXT")
    private String palavrasCiclo2;

    @Column(name = "palavras_ciclo3", columnDefinition = "TEXT")
    private String palavrasCiclo3;

    // Resultados de cada ciclo (JSON serializado)
    @Column(name = "resultado_ciclo1", columnDefinition = "TEXT")
    private String resultadoCiclo1;

    @Column(name = "resultado_ciclo2", columnDefinition = "TEXT")
    private String resultadoCiclo2;

    @Column(name = "resultado_ciclo3", columnDefinition = "TEXT")
    private String resultadoCiclo3;

    // Métricas gerais
    @Column(name = "total_palavras")
    private Integer totalPalavras;

    @Column(name = "total_acertos")
    private Integer totalAcertos;

    @Column(name = "pontuacao_geral")
    private Double pontuacaoGeral;

    // Timestamps
    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    // Conversa completa (para histórico)
    @Column(name = "historico_conversa", columnDefinition = "TEXT")
    private String historicoConversa;

    public enum StatusSessao {
        INICIADA,
        AGUARDANDO_AUDIO,
        PROCESSANDO,
        FINALIZADA,
        CANCELADA
    }

    // Construtor padrão
    public SessaoTreinoEntity() {
        this.dataInicio = LocalDateTime.now();
        this.cicloAtual = 0;
        this.status = StatusSessao.INICIADA;
        this.totalCiclos = 3;
        this.palavrasPorCiclo = 3;
        this.totalPalavras = 0;
        this.totalAcertos = 0;
        this.pontuacaoGeral = 0.0;
        this.historicoConversa = "";
    }

    // Métodos auxiliares
    public void adicionarAoHistorico(String remetente, String mensagem) {
        String timestamp = LocalDateTime.now().toString();
        String novaLinha = "[" + timestamp + "] " + remetente + ": " + mensagem + "\n";
        this.historicoConversa = (this.historicoConversa == null ? "" : this.historicoConversa) + novaLinha;
    }

    public boolean isUltimoCiclo() {
        return this.cicloAtual >= this.totalCiclos;
    }

    public void avancarCiclo() {
        this.cicloAtual++;
    }

    public String getPalavrasCicloAtual() {
        switch (this.cicloAtual) {
            case 1: return this.palavrasCiclo1;
            case 2: return this.palavrasCiclo2;
            case 3: return this.palavrasCiclo3;
            default: return null;
        }
    }

    public void setPalavrasCicloAtual(String palavras) {
        switch (this.cicloAtual) {
            case 1: this.palavrasCiclo1 = palavras; break;
            case 2: this.palavrasCiclo2 = palavras; break;
            case 3: this.palavrasCiclo3 = palavras; break;
        }
    }

    public void setResultadoCicloAtual(String resultado) {
        switch (this.cicloAtual) {
            case 1: this.resultadoCiclo1 = resultado; break;
            case 2: this.resultadoCiclo2 = resultado; break;
            case 3: this.resultadoCiclo3 = resultado; break;
        }
    }
}