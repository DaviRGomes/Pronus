package com.inatel.prototipo_ia.dto.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MensagemSessaoDtoOut {

    // Tipos de mensagem
    public enum TipoMensagem {
        SAUDACAO,           // Mensagem inicial de boas-vindas
        INSTRUCAO,          // Instrução antes das palavras
        PALAVRAS,           // Lista de palavras para falar
        FEEDBACK_CICLO,     // Feedback após análise de um ciclo
        RESUMO_FINAL,       // Resumo ao final da sessão
        AGUARDANDO_AUDIO,   // Aguardando envio do áudio
        ERRO                // Mensagem de erro
    }

    private Long sessaoId;
    private TipoMensagem tipo;
    private String mensagem;
    private List<String> palavras; // Palavras do ciclo atual (quando tipo = PALAVRAS)
    private Integer cicloAtual;
    private Integer totalCiclos;
    private ResultadoCiclo resultadoCiclo; // Resultado da análise (quando tipo = FEEDBACK_CICLO)
    private ResumoSessao resumoSessao; // Resumo final (quando tipo = RESUMO_FINAL)
    private LocalDateTime timestamp;
    private Boolean sessaoFinalizada;

    // Classe interna para resultado de cada ciclo
    @Getter
    @Setter
    public static class ResultadoCiclo {
        private Integer ciclo;
        private Integer acertos;
        private Integer total;
        private Double pontuacao;
        private List<ResultadoPalavra> detalhes;
        private String feedback;
    }

    // Classe interna para resultado de cada palavra
    @Getter
    @Setter
    public static class ResultadoPalavra {
        private String palavraEsperada;
        private String palavraTranscrita;
        private Boolean acertou;
        private Double similaridade;
        private String feedback;
    }

    // Classe interna para resumo final
    @Getter
    @Setter
    public static class ResumoSessao {
        private Integer totalPalavras;
        private Integer totalAcertos;
        private Double pontuacaoGeral;
        private Double porcentagemAcerto;
        private String feedbackGeral;
        private List<String> pontosFortes;
        private List<String> pontosAMelhorar;
        private Integer duracaoMinutos;
    }

    // Construtor padrão
    public MensagemSessaoDtoOut() {
        this.timestamp = LocalDateTime.now();
        this.sessaoFinalizada = false;
    }

    // Factory methods para criar mensagens facilmente
    public static MensagemSessaoDtoOut saudacao(Long sessaoId, String nomeCliente) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.SAUDACAO);
        msg.setMensagem("Olá, " + nomeCliente + "! 👋 Bom dia! Que bom ter você aqui para mais uma sessão de treino. " +
                "Vamos praticar juntos? Preparei algumas palavras especiais para você hoje!");
        msg.setCicloAtual(0);
        msg.setTotalCiclos(3);
        return msg;
    }

    public static MensagemSessaoDtoOut instrucao(Long sessaoId, int ciclo, int totalCiclos) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.INSTRUCAO);
        msg.setCicloAtual(ciclo);
        msg.setTotalCiclos(totalCiclos);

        String[] instrucoes = {
                "Vamos começar! 🎯 Vou te mostrar 3 palavras. Fale cada uma delas com calma, ok?",
                "Muito bem! Agora vamos para a segunda rodada. 💪 Lembre-se: respire fundo e fale devagar!",
                "Última rodada! 🌟 Você está indo super bem! Vamos lá, concentre-se nessas últimas palavras!"
        };

        msg.setMensagem(instrucoes[Math.min(ciclo - 1, instrucoes.length - 1)]);
        return msg;
    }

    public static MensagemSessaoDtoOut palavras(Long sessaoId, int ciclo, int totalCiclos, List<String> palavras) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.PALAVRAS);
        msg.setCicloAtual(ciclo);
        msg.setTotalCiclos(totalCiclos);
        msg.setPalavras(palavras);
        msg.setMensagem("Fale as seguintes palavras:");
        return msg;
    }

    public static MensagemSessaoDtoOut aguardandoAudio(Long sessaoId, int ciclo, int totalCiclos) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.AGUARDANDO_AUDIO);
        msg.setCicloAtual(ciclo);
        msg.setTotalCiclos(totalCiclos);
        msg.setMensagem("Estou ouvindo... 🎤 Grave o áudio com as palavras quando estiver pronto!");
        return msg;
    }

    public static MensagemSessaoDtoOut feedbackCiclo(Long sessaoId, int ciclo, int totalCiclos, ResultadoCiclo resultado) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.FEEDBACK_CICLO);
        msg.setCicloAtual(ciclo);
        msg.setTotalCiclos(totalCiclos);
        msg.setResultadoCiclo(resultado);

        // Gerar mensagem baseada no desempenho
        double pontuacao = resultado.getPontuacao();
        String feedback;
        if (pontuacao >= 90) {
            feedback = "Excelente! 🌟 Você mandou muito bem nessa rodada! " + resultado.getAcertos() + " de " + resultado.getTotal() + " palavras certas!";
        } else if (pontuacao >= 70) {
            feedback = "Muito bom! 👍 Você acertou " + resultado.getAcertos() + " de " + resultado.getTotal() + " palavras. Continue assim!";
        } else if (pontuacao >= 50) {
            feedback = "Bom trabalho! 💪 Acertou " + resultado.getAcertos() + " de " + resultado.getTotal() + ". Vamos melhorar na próxima!";
        } else {
            feedback = "Não desanime! 🤗 Acertou " + resultado.getAcertos() + " de " + resultado.getTotal() + ". A prática leva à perfeição!";
        }

        msg.setMensagem(feedback);
        return msg;
    }

    public static MensagemSessaoDtoOut resumoFinal(Long sessaoId, ResumoSessao resumo) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.RESUMO_FINAL);
        msg.setResumoSessao(resumo);
        msg.setSessaoFinalizada(true);

        String feedback;
        if (resumo.getPontuacaoGeral() >= 80) {
            feedback = "🎉 Parabéns! Sessão finalizada com sucesso! Você foi incrível hoje! " +
                    "Acertou " + resumo.getTotalAcertos() + " de " + resumo.getTotalPalavras() + " palavras (" +
                    String.format("%.0f", resumo.getPorcentagemAcerto()) + "%). Até a próxima sessão!";
        } else if (resumo.getPontuacaoGeral() >= 60) {
            feedback = "👏 Muito bem! Sessão concluída! Você acertou " + resumo.getTotalAcertos() +
                    " de " + resumo.getTotalPalavras() + " palavras. Continue praticando e vai melhorar cada vez mais!";
        } else {
            feedback = "💪 Sessão finalizada! Não desanime, você acertou " + resumo.getTotalAcertos() +
                    " de " + resumo.getTotalPalavras() + " palavras. A prática constante vai te ajudar muito!";
        }

        msg.setMensagem(feedback);
        return msg;
    }

    public static MensagemSessaoDtoOut erro(Long sessaoId, String mensagemErro) {
        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(TipoMensagem.ERRO);
        msg.setMensagem("Ops! 😅 " + mensagemErro);
        return msg;
    }
}