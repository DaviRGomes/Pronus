package com.inatel.prototipo_ia.dto.out;

import java.time.LocalDateTime;

public class PronunciationAnalysisDTO {

    private String palavraEsperada;
    private String palavraTranscrita;
    private Double pontuacao; // 0.0 a 100.0
    private Double confianca; // 0.0 a 1.0 da API
    private String feedback;
    private Boolean acertou;
    private LocalDateTime dataAnalise;

    // Construtor vazio
    public PronunciationAnalysisDTO() {
        this.dataAnalise = LocalDateTime.now();
    }

    // Construtor completo
    public PronunciationAnalysisDTO(String palavraEsperada, String palavraTranscrita,
                                    Double pontuacao, Double confianca,
                                    String feedback, Boolean acertou) {
        this.palavraEsperada = palavraEsperada;
        this.palavraTranscrita = palavraTranscrita;
        this.pontuacao = pontuacao;
        this.confianca = confianca;
        this.feedback = feedback;
        this.acertou = acertou;
        this.dataAnalise = LocalDateTime.now();
    }

    // Getters e Setters
    public String getPalavraEsperada() {
        return palavraEsperada;
    }

    public void setPalavraEsperada(String palavraEsperada) {
        this.palavraEsperada = palavraEsperada;
    }

    public String getPalavraTranscrita() {
        return palavraTranscrita;
    }

    public void setPalavraTranscrita(String palavraTranscrita) {
        this.palavraTranscrita = palavraTranscrita;
    }

    public Double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Double pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Double getConfianca() {
        return confianca;
    }

    public void setConfianca(Double confianca) {
        this.confianca = confianca;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Boolean getAcertou() {
        return acertou;
    }

    public void setAcertou(Boolean acertou) {
        this.acertou = acertou;
    }

    public LocalDateTime getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(LocalDateTime dataAnalise) {
        this.dataAnalise = dataAnalise;
    }
}
