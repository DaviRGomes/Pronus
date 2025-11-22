package com.inatel.prototipo_ia.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inatel.prototipo_ia.dto.out.BatchPronunciationAnalysisDTO;
import okhttp3.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PronunciationAnalysisService {

    @Value("${deepgram.api.key}")
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

    public BatchPronunciationAnalysisDTO analisarPronunciaEmLote(byte[] audioBytes, List<String> palavrasEsperadas) {
        try {
            String transcricaoCompleta = transcreverAudio(audioBytes, palavrasEsperadas);
            System.out.println("Transcrição Otimizada: " + transcricaoCompleta);

        
            List<String> palavrasTranscritas = Arrays.stream(transcricaoCompleta.split("\\s+"))
                    .map(this::normalizarTexto)
                    .filter(p -> !p.isEmpty())
                    .collect(Collectors.toList());

            
            List<BatchPronunciationAnalysisDTO.ResultadoPalavra> resultados = new ArrayList<>();
            int acertos = 0;
            double somaSimilaridades = 0.0;

            boolean[] palavrasUsadas = new boolean[palavrasTranscritas.size()];

            for (String palavraEsperada : palavrasEsperadas) {
                String pEsperadaNorm = normalizarTexto(palavraEsperada);
                
                String melhorPalavraEncontrada = "";
                double melhorScore = 0.0;
                int indiceMelhorMatch = -1;

            
                for (int i = 0; i < palavrasTranscritas.size(); i++) {
                    if (palavrasUsadas[i]) continue;

                    String pTranscrita = palavrasTranscritas.get(i);
                    double score = calcularSimilaridade(pEsperadaNorm, pTranscrita);

                    if (score > melhorScore) {
                        melhorScore = score;
                        melhorPalavraEncontrada = pTranscrita;
                        indiceMelhorMatch = i;
                    }
                }

                
                if (indiceMelhorMatch != -1 && melhorScore >= 0.6) {
                    palavrasUsadas[indiceMelhorMatch] = true;
                }

                boolean acertou = melhorScore >= 0.6; 
                if (acertou) acertos++;
                somaSimilaridades += melhorScore;

                String feedback = gerarFeedbackPalavra(melhorScore, palavraEsperada, melhorPalavraEncontrada);

                resultados.add(new BatchPronunciationAnalysisDTO.ResultadoPalavra(
                        palavraEsperada,
                        melhorPalavraEncontrada.isEmpty() ? "(não detectada)" : melhorPalavraEncontrada,
                        acertou,
                        melhorScore * 100,
                        feedback
                ));
            }

            
            double pontuacaoGeral = palavrasEsperadas.isEmpty() ? 0.0 : (somaSimilaridades / palavrasEsperadas.size()) * 100;
            double porcentagemAcerto = palavrasEsperadas.isEmpty() ? 0.0 : ((double) acertos / palavrasEsperadas.size()) * 100;

            String feedbackGeral = gerarFeedbackGeral(acertos, palavrasEsperadas.size(), pontuacaoGeral);

            BatchPronunciationAnalysisDTO resultado = new BatchPronunciationAnalysisDTO();
            resultado.setPalavrasEsperadas(palavrasEsperadas);
            resultado.setTranscricaoCompleta(transcricaoCompleta);
            resultado.setResultados(resultados);
            resultado.setPontuacaoGeral(pontuacaoGeral);
            resultado.setTotalAcertos(acertos);
            resultado.setTotalPalavras(palavrasEsperadas.size());
            resultado.setPorcentagemAcerto(porcentagemAcerto);
            resultado.setFeedbackGeral(feedbackGeral);

            return resultado;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao analisar pronúncia: " + e.getMessage(), e);
        }
    }

    private String transcreverAudio(byte[] audioBytes, List<String> palavrasChave) throws IOException {
        RequestBody body = RequestBody.create(audioBytes, MediaType.parse("audio/*"));

        // Constroi a URL base
        StringBuilder urlBuilder = new StringBuilder("https://api.deepgram.com/v1/listen?model=nova-2&language=pt-BR&smart_format=true&punctuate=false&diarize=false");
        
       
        if (palavrasChave != null) {
            for (String palavra : palavrasChave) {
                try {
                    String encodedWord = URLEncoder.encode(palavra.trim(), StandardCharsets.UTF_8.toString());
                    
                    if (!encodedWord.isEmpty()) {
                        urlBuilder.append("&keywords=").append(encodedWord).append(":2.0");
                    }
                } catch (Exception e) {
                    
                }
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .addHeader("Authorization", "Token " + deepgramApiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Sem detalhes";
                throw new IOException("Erro Deepgram: " + response.code() + " - " + errorBody);
            }

            String jsonResponse = response.body().string();
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

            if (!json.has("results")) return "";

            try {
                 return json.getAsJsonObject("results")
                    .getAsJsonArray("channels")
                    .get(0).getAsJsonObject()
                    .getAsJsonArray("alternatives")
                    .get(0).getAsJsonObject()
                    .get("transcript").getAsString();
            } catch (Exception e) {
                return "";
            }
        }
    }

    private double calcularSimilaridade(String p1, String p2) {
        if (p1.equals(p2)) return 1.0;
        if (p1.isEmpty() || p2.isEmpty()) return 0.0;

    
        if (p1.length() > 4 && p2.length() > 4) {
             if (p1.startsWith(p2) || p2.startsWith(p1)) return 0.95;
        }

        int distancia = levenshtein.apply(p1, p2);
        int maxLen = Math.max(p1.length(), p2.length());
        return 1.0 - ((double) distancia / maxLen);
    }

    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[^\\p{ASCII}]", "");
        normalized = normalized.replaceAll("[.,!?]", ""); 
        return normalized.toLowerCase().trim();
    }

    private String gerarFeedbackPalavra(double similaridade, String esperada, String transcrita) {
        double pontuacao = similaridade * 100;
        if (pontuacao >= 90) return "Perfeito! ✅";
        else if (pontuacao >= 60) return "Muito bom! 👏";
        else return "Tente falar mais devagar (ouvi: '" + transcrita + "') 🗣️";
    }

    private String gerarFeedbackGeral(int acertos, int total, double pontuacaoGeral) {
        if (pontuacaoGeral >= 85) return "Excelente! Sua pronúncia está ótima. 🎉";
        else if (pontuacaoGeral >= 60) return "Muito bom! Continue praticando. 👏";
        else return "Vamos treinar mais um pouco? Tente falar mais perto do microfone. 💪";
    }
}