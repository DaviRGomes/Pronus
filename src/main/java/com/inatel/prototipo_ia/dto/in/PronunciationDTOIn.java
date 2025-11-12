package com.inatel.prototipo_ia.dto.in;

import org.springframework.web.multipart.MultipartFile;

public class PronunciationDTOIn {

    private MultipartFile audio;
    private String palavraEsperada;
    private Long usuarioId; // opcional - para vincular ao usuário
    private Long sessaoId; // opcional - para agrupar análises de uma mesma sessão

    // Construtor vazio
    public PronunciationDTOIn() {

    }

    // Construtor com campos essenciais
    public PronunciationDTOIn(MultipartFile audio, String palavraEsperada) {
        this.audio = audio;
        this.palavraEsperada = palavraEsperada;
    }

    // Getters e Setters
    public MultipartFile getAudio() {
        return audio;
    }

    public void setAudio(MultipartFile audio) {
        this.audio = audio;
    }

    public String getPalavraEsperada() {
        return palavraEsperada;
    }

    public void setPalavraEsperada(String palavraEsperada) {
        this.palavraEsperada = palavraEsperada;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getSessaoId() {
        return sessaoId;
    }

    public void setSessaoId(Long sessaoId) {
        this.sessaoId = sessaoId;
    }

    // Validação básica
    public boolean isValid() {
        return audio != null &&
                !audio.isEmpty() &&
                palavraEsperada != null &&
                !palavraEsperada.trim().isEmpty();
    }
}
