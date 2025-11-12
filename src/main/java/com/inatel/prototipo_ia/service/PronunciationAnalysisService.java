package com.inatel.prototipo_ia.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inatel.prototipo_ia.dto.out.PronunciationAnalysisDTO;
import okhttp3.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.Normalizer;
import java.util.concurrent.TimeUnit;

@Service
public class PronunciationAnalysisService {

    @Value("${deepgram.api.key:sua-chave-aqui}")
    private String deepgramApiKey;

    private final OkHttpClient httpClient;
    private final LevenshteinDistance levenshtein;

    public PronunciationAnalysisService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.levenshtein = new LevenshteinDistance();
    }

    /**
     * Analisa a pronúncia do áudio enviado
     * @param audioBytes bytes do arquivo de áudio
     * @param palavraEsperada palavra que deveria ser pronunciada
     * @return análise completa da pronúncia
     */
    public PronunciationAnalysisDTO analisarPronuncia(byte[] audioBytes, String palavraEsperada) {
        try {
            // 1. Transcrever o áudio
            String transcricao = transcreverAudio(audioBytes);

            // 2. Calcular similaridade
            double similaridade = calcularSimilaridade(palavraEsperada, transcricao);

            // 3. Gerar pontuação
            double pontuacao = similaridade * 100;

            // 4. Determinar se acertou
            boolean acertou = pontuacao >= 70.0;

            // 5. Gerar feedback
            String feedback = gerarFeedback(pontuacao, palavraEsperada, transcricao);

            // 6. Criar DTO de resposta
            return new PronunciationAnalysisDTO(
                    palavraEsperada,
                    transcricao,
                    pontuacao,
                    similaridade,
                    feedback,
                    acertou
            );

        } catch (Exception e) {
            throw new RuntimeException("Erro ao analisar pronúncia: " + e.getMessage(), e);
        }
    }

    /**
     * Transcreve o áudio usando Deepgram API
     */
    private String transcreverAudio(byte[] audioBytes) throws IOException {
        RequestBody body = RequestBody.create(audioBytes, MediaType.parse("audio/wav"));

        Request request = new Request.Builder()
                .url("https://api.deepgram.com/v1/listen?language=pt-BR&punctuate=false&diarize=false")
                .addHeader("Authorization", "Token " + deepgramApiKey)
                .addHeader("Content-Type", "audio/wav")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro na API: " + response.code());
            }

            String jsonResponse = response.body().string();
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Extrair transcrição
            String transcript = json.getAsJsonObject("results")
                    .getAsJsonArray("channels")
                    .get(0).getAsJsonObject()
                    .getAsJsonArray("alternatives")
                    .get(0).getAsJsonObject()
                    .get("transcript").getAsString();

            return normalizarTexto(transcript);
        }
    }

    /**
     * Calcula similaridade entre duas palavras
     */
    private double calcularSimilaridade(String palavra1, String palavra2) {
        String p1 = normalizarTexto(palavra1);
        String p2 = normalizarTexto(palavra2);

        // Se forem exatamente iguais
        if (p1.equals(p2)) {
            return 1.0;
        }

        // Calcular distância de Levenshtein
        int distancia = levenshtein.apply(p1, p2);
        int maxLen = Math.max(p1.length(), p2.length());

        // Converter para similaridade (0.0 a 1.0)
        return 1.0 - ((double) distancia / maxLen);
    }

    /**
     * Remove acentos e normaliza texto
     */
    private String normalizarTexto(String texto) {
        if (texto == null) return "";

        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        return normalized.toLowerCase().trim();
    }

    /**
     * Gera feedback personalizado baseado na pontuação
     */
    private String gerarFeedback(double pontuacao, String esperada, String transcrita) {
        if (pontuacao >= 95) {
            return "Excelente! Pronúncia perfeita! 🎉";
        } else if (pontuacao >= 85) {
            return "Muito bom! Quase perfeito! Continue assim! 👏";
        } else if (pontuacao >= 70) {
            return "Bom trabalho! Você está no caminho certo! 👍";
        } else if (pontuacao >= 50) {
            return "Quase lá! Tente pronunciar mais devagar: '" + esperada + "'";
        } else {
            return "Vamos tentar novamente? A palavra é: '" + esperada + "'. " +
                    "Você disse: '" + transcrita + "'";
        }
    }
}