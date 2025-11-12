package com.inatel.prototipo_ia.controller;

import com.inatel.prototipo_ia.dto.out.PronunciationAnalysisDTO;
import com.inatel.prototipo_ia.service.PronunciationAnalysisService;
import com.inatel.prototipo_ia.service.WordGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pronunciation")
@CrossOrigin(origins = "*")
public class PronunciationController {

    @Autowired
    private PronunciationAnalysisService pronunciationService;

    @Autowired
    private WordGeneratorService wordGeneratorService;

    /**
     * Endpoint para analisar pronúncia
     * POST /api/pronunciation/analyze
     *
     * @param audioFile arquivo de áudio (WAV, MP3, etc)
     * @param palavraEsperada palavra que deveria ser pronunciada
     * @param usuarioId (opcional) ID do usuário
     * @param sessaoId (opcional) ID da sessão
     * @return PronunciationAnalysisDTO com resultado da análise
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analisarPronuncia(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("palavraEsperada") String palavraEsperada,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long sessaoId) {

        try {
            // Validações
            if (audioFile == null || audioFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(criarErro("Arquivo de áudio não pode estar vazio"));
            }

            if (palavraEsperada == null || palavraEsperada.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(criarErro("Palavra esperada não pode estar vazia"));
            }

            // Converter para bytes
            byte[] audioBytes = audioFile.getBytes();

            // Analisar pronúncia
            PronunciationAnalysisDTO resultado = pronunciationService
                    .analisarPronuncia(audioBytes, palavraEsperada);

            // Aqui você pode salvar no banco usando usuarioId e sessaoId
            // exemplo: salvarResultado(resultado, usuarioId, sessaoId);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErro("Erro ao processar áudio: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para gerar palavras personalizadas
     * GET /api/pronunciation/words
     *
     * @param idade idade do usuário
     * @param dificuldade tipo de dificuldade (R, L, S, CH, LH)
     * @param quantidade quantas palavras gerar (padrão: 10)
     * @return lista de palavras
     */
    @GetMapping("/words")
    public ResponseEntity<?> gerarPalavras(
            @RequestParam int idade,
            @RequestParam String dificuldade,
            @RequestParam(defaultValue = "10") int quantidade) {

        try {
            List<String> palavras = wordGeneratorService
                    .gerarPalavrasPersonalizadas(idade, dificuldade, quantidade);

            Map<String, Object> response = new HashMap<>();
            response.put("palavras", palavras);
            response.put("total", palavras.size());
            response.put("idade", idade);
            response.put("dificuldade", dificuldade);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(criarErro("Erro ao gerar palavras: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para listar dificuldades disponíveis
     * GET /api/pronunciation/difficulties
     */
    @GetMapping("/difficulties")
    public ResponseEntity<List<String>> listarDificuldades() {
        return ResponseEntity.ok(wordGeneratorService.getDificuldadesDisponiveis());
    }

    /**
     * Health check da API de pronúncia
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Pronunciation Analysis API");
        return ResponseEntity.ok(status);
    }

    /**
     * Helper para criar mensagens de erro padronizadas
     */
    private Map<String, String> criarErro(String mensagem) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", mensagem);
        erro.put("timestamp", java.time.LocalDateTime.now().toString());
        return erro;
    }
}