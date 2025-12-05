package com.inatel.prototipo_ia.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inatel.prototipo_ia.dto.in.SessaoTreinoDtoIn;
import com.inatel.prototipo_ia.dto.out.BatchPronunciationAnalysisDTO;
import com.inatel.prototipo_ia.dto.out.MensagemSessaoDtoOut;
import com.inatel.prototipo_ia.dto.out.MensagemSessaoDtoOut.*;
import com.inatel.prototipo_ia.entity.*;
import com.inatel.prototipo_ia.entity.SessaoTreinoEntity.StatusSessao;
import com.inatel.prototipo_ia.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessaoTreinoService {

    private final SessaoTreinoRepository sessaoRepository;
    private final ClienteRepository clienteRepository;
    private final EspecialistaRepository especialistaRepository;
    private final ChatRepository chatRepository;
    private final AIWordGeneratorService wordGeneratorService;
    private final PronunciationAnalysisService pronunciationService;
    private final GeminiAudioAnalysisService geminiService;
    private final Gson gson;

    public SessaoTreinoService(
            SessaoTreinoRepository sessaoRepository,
            ClienteRepository clienteRepository,
            EspecialistaRepository especialistaRepository,
            ChatRepository chatRepository,
            AIWordGeneratorService wordGeneratorService,
            PronunciationAnalysisService pronunciationService,
            GeminiAudioAnalysisService geminiService) {
        this.sessaoRepository = sessaoRepository;
        this.clienteRepository = clienteRepository;
        this.especialistaRepository = especialistaRepository;
        this.chatRepository = chatRepository;
        this.wordGeneratorService = wordGeneratorService;
        this.pronunciationService = pronunciationService;
        this.geminiService = geminiService;
        this.gson = new Gson();
    }

    /**
     * PASSO 1: Inicia uma nova sessão de treino
     * Retorna a saudação inicial
     */
    public List<MensagemSessaoDtoOut> iniciarSessao(SessaoTreinoDtoIn dto) {
        List<MensagemSessaoDtoOut> mensagens = new ArrayList<>();

        // Validações
        ClienteEntity cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + dto.getClienteId()));

        EspecialistaEntity especialista = especialistaRepository.findById(dto.getEspecialistaId())
                .orElseThrow(() -> new EntityNotFoundException("Especialista não encontrado: " + dto.getEspecialistaId()));

        // Verifica se já tem sessão ativa
        List<StatusSessao> statusAtivos = Arrays.asList(StatusSessao.INICIADA, StatusSessao.AGUARDANDO_AUDIO, StatusSessao.PROCESSANDO);
        if (sessaoRepository.existsByClienteIdAndStatusIn(dto.getClienteId(), statusAtivos)) {
            // Recupera sessão existente
            List<SessaoTreinoEntity> sessoesAtivas = sessaoRepository.findSessoesAtivasByClienteId(dto.getClienteId());
            if (!sessoesAtivas.isEmpty()) {
                return recuperarEstadoSessao(sessoesAtivas.get(0));
            }
        }

        // Cria nova sessão
        SessaoTreinoEntity sessao = new SessaoTreinoEntity();
        sessao.setCliente(cliente);
        sessao.setEspecialista(especialista);
        sessao.setDificuldade(dto.getDificuldade() != null ? dto.getDificuldade() : "GERAL");
        sessao.setIdadeCliente(dto.getIdade() != null ? dto.getIdade() : cliente.getIdade());
        sessao.setTotalCiclos(3);
        sessao.setPalavrasPorCiclo(3);

        // Gera todas as palavras da sessão de uma vez (9 palavras para 3 ciclos)
        List<String> todasPalavras = wordGeneratorService.gerarPalavrasComIA(
                sessao.getIdadeCliente(),
                sessao.getDificuldade(),
                sessao.getTotalCiclos() * sessao.getPalavrasPorCiclo()
        );

        // Divide em ciclos
        sessao.setPalavrasCiclo1(gson.toJson(todasPalavras.subList(0, 3)));
        sessao.setPalavrasCiclo2(gson.toJson(todasPalavras.subList(3, 6)));
        sessao.setPalavrasCiclo3(gson.toJson(todasPalavras.subList(6, 9)));

        // Salva sessão
        sessao = sessaoRepository.save(sessao);

        // Monta mensagens de saudação
        MensagemSessaoDtoOut saudacao = MensagemSessaoDtoOut.saudacao(sessao.getId(), cliente.getNome());
        sessao.adicionarAoHistorico("SISTEMA", saudacao.getMensagem());
        mensagens.add(saudacao);

        // Já avança para o primeiro ciclo
        sessao.avancarCiclo(); // ciclo = 1

        // Adiciona instrução
        MensagemSessaoDtoOut instrucao = MensagemSessaoDtoOut.instrucao(sessao.getId(), sessao.getCicloAtual(), sessao.getTotalCiclos());
        sessao.adicionarAoHistorico("SISTEMA", instrucao.getMensagem());
        mensagens.add(instrucao);

        // Adiciona palavras do primeiro ciclo
        List<String> palavrasCiclo1 = gson.fromJson(sessao.getPalavrasCiclo1(), new TypeToken<List<String>>(){}.getType());
        MensagemSessaoDtoOut palavras = MensagemSessaoDtoOut.palavras(sessao.getId(), sessao.getCicloAtual(), sessao.getTotalCiclos(), palavrasCiclo1);
        sessao.adicionarAoHistorico("SISTEMA", "Palavras: " + String.join(", ", palavrasCiclo1));
        mensagens.add(palavras);

        // Mensagem aguardando áudio
        MensagemSessaoDtoOut aguardando = MensagemSessaoDtoOut.aguardandoAudio(sessao.getId(), sessao.getCicloAtual(), sessao.getTotalCiclos());
        mensagens.add(aguardando);

        // Atualiza status
        sessao.setStatus(StatusSessao.AGUARDANDO_AUDIO);
        sessaoRepository.save(sessao);

        return mensagens;
    }

    /**
     * PASSO 2: Processa o áudio enviado pelo cliente
     * Retorna feedback + próximas palavras (ou resumo final)
     */
    public List<MensagemSessaoDtoOut> processarAudio(Long sessaoId, byte[] audioBytes, boolean usarGemini) {
        List<MensagemSessaoDtoOut> mensagens = new ArrayList<>();

        SessaoTreinoEntity sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada: " + sessaoId));

        if (sessao.getStatus() != StatusSessao.AGUARDANDO_AUDIO) {
            mensagens.add(MensagemSessaoDtoOut.erro(sessaoId, "Sessão não está aguardando áudio. Status atual: " + sessao.getStatus()));
            return mensagens;
        }

        sessao.setStatus(StatusSessao.PROCESSANDO);

        try {
            // Pega palavras do ciclo atual
            List<String> palavrasEsperadas = getPalavrasCiclo(sessao, sessao.getCicloAtual());

            // Analisa pronúncia
            BatchPronunciationAnalysisDTO resultado;
            if (usarGemini) {
                resultado = geminiService.analisarPronunciaEmLote(audioBytes, palavrasEsperadas);
            } else {
                resultado = pronunciationService.analisarPronunciaEmLote(audioBytes, palavrasEsperadas);
            }

            // Registra no histórico
            sessao.adicionarAoHistorico("CLIENTE", "[ÁUDIO ENVIADO]");

            // Converte resultado para nosso formato
            ResultadoCiclo resultadoCiclo = converterResultado(sessao.getCicloAtual(), resultado);

            // Salva resultado do ciclo
            sessao.setResultadoCicloAtual(gson.toJson(resultadoCiclo));

            // Atualiza totais
            sessao.setTotalPalavras(sessao.getTotalPalavras() + resultadoCiclo.getTotal());
            sessao.setTotalAcertos(sessao.getTotalAcertos() + resultadoCiclo.getAcertos());

            // Monta mensagem de feedback
            MensagemSessaoDtoOut feedback = MensagemSessaoDtoOut.feedbackCiclo(
                    sessaoId, sessao.getCicloAtual(), sessao.getTotalCiclos(), resultadoCiclo);
            sessao.adicionarAoHistorico("SISTEMA", feedback.getMensagem());
            mensagens.add(feedback);

            // Verifica se é o último ciclo
            if (sessao.isUltimoCiclo()) {
                // Finaliza sessão
                return finalizarSessao(sessao, mensagens);
            } else {
                // Avança para próximo ciclo
                sessao.avancarCiclo();

                // Pausa dramática (mensagem de transição)
                MensagemSessaoDtoOut transicao = new MensagemSessaoDtoOut();
                transicao.setSessaoId(sessaoId);
                transicao.setTipo(MensagemSessaoDtoOut.TipoMensagem.INSTRUCAO);
                transicao.setCicloAtual(sessao.getCicloAtual());
                transicao.setTotalCiclos(sessao.getTotalCiclos());
                transicao.setMensagem("Ótimo! Vamos para a próxima rodada... 🎯");
                mensagens.add(transicao);

                // Adiciona instrução do novo ciclo
                MensagemSessaoDtoOut instrucao = MensagemSessaoDtoOut.instrucao(
                        sessaoId, sessao.getCicloAtual(), sessao.getTotalCiclos());
                sessao.adicionarAoHistorico("SISTEMA", instrucao.getMensagem());
                mensagens.add(instrucao);

                // Adiciona palavras do novo ciclo
                List<String> novasPalavras = getPalavrasCiclo(sessao, sessao.getCicloAtual());
                MensagemSessaoDtoOut palavras = MensagemSessaoDtoOut.palavras(
                        sessaoId, sessao.getCicloAtual(), sessao.getTotalCiclos(), novasPalavras);
                sessao.adicionarAoHistorico("SISTEMA", "Palavras: " + String.join(", ", novasPalavras));
                mensagens.add(palavras);

                // Aguardando áudio
                MensagemSessaoDtoOut aguardando = MensagemSessaoDtoOut.aguardandoAudio(
                        sessaoId, sessao.getCicloAtual(), sessao.getTotalCiclos());
                mensagens.add(aguardando);

                sessao.setStatus(StatusSessao.AGUARDANDO_AUDIO);
            }

            sessaoRepository.save(sessao);

        } catch (Exception e) {
            sessao.setStatus(StatusSessao.AGUARDANDO_AUDIO); // Volta para aguardando
            sessaoRepository.save(sessao);
            mensagens.add(MensagemSessaoDtoOut.erro(sessaoId, "Erro ao processar áudio: " + e.getMessage() + ". Por favor, tente enviar novamente."));
        }

        return mensagens;
    }

    /**
     * Finaliza a sessão e retorna resumo
     */
    private List<MensagemSessaoDtoOut> finalizarSessao(SessaoTreinoEntity sessao, List<MensagemSessaoDtoOut> mensagens) {
        sessao.setStatus(StatusSessao.FINALIZADA);
        sessao.setDataFim(LocalDateTime.now());

        // Calcula pontuação geral
        double pontuacaoGeral = sessao.getTotalPalavras() > 0
                ? ((double) sessao.getTotalAcertos() / sessao.getTotalPalavras()) * 100
                : 0;
        sessao.setPontuacaoGeral(pontuacaoGeral);

        // Monta resumo
        ResumoSessao resumo = new ResumoSessao();
        resumo.setTotalPalavras(sessao.getTotalPalavras());
        resumo.setTotalAcertos(sessao.getTotalAcertos());
        resumo.setPontuacaoGeral(pontuacaoGeral);
        resumo.setPorcentagemAcerto(pontuacaoGeral);
        resumo.setDuracaoMinutos((int) Duration.between(sessao.getDataInicio(), sessao.getDataFim()).toMinutes());

        // Analisa pontos fortes e fracos
        List<String> pontosFortes = new ArrayList<>();
        List<String> pontosAMelhorar = new ArrayList<>();

        if (pontuacaoGeral >= 80) {
            pontosFortes.add("Excelente articulação geral");
            pontosFortes.add("Boa pronúncia do fonema " + sessao.getDificuldade());
        } else if (pontuacaoGeral >= 60) {
            pontosFortes.add("Boa evolução durante a sessão");
            pontosAMelhorar.add("Pratique mais o fonema " + sessao.getDificuldade());
        } else {
            pontosAMelhorar.add("Foque na articulação do fonema " + sessao.getDificuldade());
            pontosAMelhorar.add("Pratique falar mais devagar");
        }

        resumo.setPontosFortes(pontosFortes);
        resumo.setPontosAMelhorar(pontosAMelhorar);

        // Gera feedback geral
        resumo.setFeedbackGeral(gerarFeedbackGeral(pontuacaoGeral, sessao.getDificuldade()));

        // Adiciona mensagem final
        MensagemSessaoDtoOut msgFinal = MensagemSessaoDtoOut.resumoFinal(sessao.getId(), resumo);
        sessao.adicionarAoHistorico("SISTEMA", msgFinal.getMensagem());
        mensagens.add(msgFinal);

        sessaoRepository.save(sessao);

        return mensagens;
    }

    /**
     * Recupera estado de uma sessão existente
     */
    private List<MensagemSessaoDtoOut> recuperarEstadoSessao(SessaoTreinoEntity sessao) {
        List<MensagemSessaoDtoOut> mensagens = new ArrayList<>();

        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessao.getId());
        msg.setTipo(MensagemSessaoDtoOut.TipoMensagem.INSTRUCAO);
        msg.setCicloAtual(sessao.getCicloAtual());
        msg.setTotalCiclos(sessao.getTotalCiclos());
        msg.setMensagem("Ei, você tem uma sessão em andamento! 👋 Vamos continuar de onde paramos?");
        mensagens.add(msg);

        // Adiciona palavras atuais
        List<String> palavrasAtuais = getPalavrasCiclo(sessao, sessao.getCicloAtual());
        MensagemSessaoDtoOut palavras = MensagemSessaoDtoOut.palavras(
                sessao.getId(), sessao.getCicloAtual(), sessao.getTotalCiclos(), palavrasAtuais);
        mensagens.add(palavras);

        MensagemSessaoDtoOut aguardando = MensagemSessaoDtoOut.aguardandoAudio(
                sessao.getId(), sessao.getCicloAtual(), sessao.getTotalCiclos());
        mensagens.add(aguardando);

        return mensagens;
    }

    /**
     * Busca estado atual da sessão
     */
    public MensagemSessaoDtoOut buscarEstadoSessao(Long sessaoId) {
        SessaoTreinoEntity sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada: " + sessaoId));

        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setCicloAtual(sessao.getCicloAtual());
        msg.setTotalCiclos(sessao.getTotalCiclos());
        msg.setSessaoFinalizada(sessao.getStatus() == StatusSessao.FINALIZADA);

        if (sessao.getStatus() == StatusSessao.AGUARDANDO_AUDIO) {
            msg.setTipo(MensagemSessaoDtoOut.TipoMensagem.AGUARDANDO_AUDIO);
            msg.setPalavras(getPalavrasCiclo(sessao, sessao.getCicloAtual()));
            msg.setMensagem("Aguardando seu áudio... 🎤");
        } else if (sessao.getStatus() == StatusSessao.FINALIZADA) {
            msg.setTipo(MensagemSessaoDtoOut.TipoMensagem.RESUMO_FINAL);
            msg.setMensagem("Sessão finalizada! Pontuação: " + String.format("%.0f", sessao.getPontuacaoGeral()) + "%");
        } else {
            msg.setTipo(MensagemSessaoDtoOut.TipoMensagem.INSTRUCAO);
            msg.setMensagem("Status: " + sessao.getStatus());
        }

        return msg;
    }

    /**
     * Cancela uma sessão
     */
    public MensagemSessaoDtoOut cancelarSessao(Long sessaoId) {
        SessaoTreinoEntity sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new EntityNotFoundException("Sessão não encontrada: " + sessaoId));

        sessao.setStatus(StatusSessao.CANCELADA);
        sessao.setDataFim(LocalDateTime.now());
        sessao.adicionarAoHistorico("SISTEMA", "Sessão cancelada pelo usuário");
        sessaoRepository.save(sessao);

        MensagemSessaoDtoOut msg = new MensagemSessaoDtoOut();
        msg.setSessaoId(sessaoId);
        msg.setTipo(MensagemSessaoDtoOut.TipoMensagem.INSTRUCAO);
        msg.setMensagem("Sessão cancelada. Até a próxima! 👋");
        msg.setSessaoFinalizada(true);
        return msg;
    }

    // ========== MÉTODOS AUXILIARES ==========

    private List<String> getPalavrasCiclo(SessaoTreinoEntity sessao, int ciclo) {
        String json;
        switch (ciclo) {
            case 1: json = sessao.getPalavrasCiclo1(); break;
            case 2: json = sessao.getPalavrasCiclo2(); break;
            case 3: json = sessao.getPalavrasCiclo3(); break;
            default: return Collections.emptyList();
        }
        if (json == null) return Collections.emptyList();
        Type listType = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, listType);
    }

    private ResultadoCiclo converterResultado(int ciclo, BatchPronunciationAnalysisDTO batch) {
        ResultadoCiclo rc = new ResultadoCiclo();
        rc.setCiclo(ciclo);
        rc.setTotal(batch.getTotalPalavras() != null ? batch.getTotalPalavras() : 0);
        rc.setAcertos(batch.getTotalAcertos() != null ? batch.getTotalAcertos() : 0);
        rc.setPontuacao(batch.getPontuacaoGeral() != null ? batch.getPontuacaoGeral() : 0.0);
        rc.setFeedback(batch.getFeedbackGeral());

        if (batch.getResultados() != null) {
            List<ResultadoPalavra> detalhes = batch.getResultados().stream()
                    .map(r -> {
                        ResultadoPalavra rp = new ResultadoPalavra();
                        rp.setPalavraEsperada(r.getPalavraEsperada());
                        rp.setPalavraTranscrita(r.getPalavraTranscrita());
                        rp.setAcertou(r.getAcertou());
                        rp.setSimilaridade(r.getSimilaridade());
                        rp.setFeedback(r.getFeedback());
                        return rp;
                    })
                    .collect(Collectors.toList());
            rc.setDetalhes(detalhes);
        }

        return rc;
    }

    private String gerarFeedbackGeral(double pontuacao, String dificuldade) {
        if (pontuacao >= 90) {
            return "Fantástico! Você dominou o fonema " + dificuldade + " hoje! Continue assim!";
        } else if (pontuacao >= 70) {
            return "Muito bom! Você está evoluindo bem com o fonema " + dificuldade + ". Pratique um pouco mais!";
        } else if (pontuacao >= 50) {
            return "Bom progresso! O fonema " + dificuldade + " é desafiador, mas você está no caminho certo!";
        } else {
            return "Continue praticando! O fonema " + dificuldade + " requer treino, mas você vai conseguir!";
        }
    }
}