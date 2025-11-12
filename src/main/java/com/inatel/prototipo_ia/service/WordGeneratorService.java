package com.inatel.prototipo_ia.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class WordGeneratorService {

    // Banco de palavras categorizado
    private final Map<String, Map<String, List<String>>> bancoPalavras;

    public WordGeneratorService() {
        this.bancoPalavras = inicializarBancoPalavras();
    }

    /**
     * Gera lista de palavras personalizada baseada no perfil do usuário
     * @param idade idade do usuário
     * @param dificuldade tipo de dificuldade (R, L, S, CH, LH, etc)
     * @param quantidade quantas palavras gerar
     * @return lista de palavras
     */
    public List<String> gerarPalavrasPersonalizadas(int idade, String dificuldade, int quantidade) {
        String faixaEtaria = determinarFaixaEtaria(idade);

        List<String> palavras = bancoPalavras
                .getOrDefault(faixaEtaria, bancoPalavras.get("infantil"))
                .getOrDefault(dificuldade.toUpperCase(), new ArrayList<>());

        if (palavras.isEmpty()) {
            palavras = palavrasGenericas(faixaEtaria);
        }

        // Embaralhar e pegar N palavras
        List<String> palavrasSelecionadas = new ArrayList<>(palavras);
        Collections.shuffle(palavrasSelecionadas);

        return palavrasSelecionadas.subList(0, Math.min(quantidade, palavrasSelecionadas.size()));
    }

    /**
     * Determina faixa etária baseada na idade
     */
    private String determinarFaixaEtaria(int idade) {
        if (idade <= 6) return "infantil";
        if (idade <= 12) return "juvenil";
        return "adulto";
    }

    /**
     * Inicializa banco de palavras por faixa etária e dificuldade
     */
    private Map<String, Map<String, List<String>>> inicializarBancoPalavras() {
        Map<String, Map<String, List<String>>> banco = new HashMap<>();

        // FAIXA INFANTIL (até 6 anos)
        Map<String, List<String>> infantil = new HashMap<>();
        infantil.put("R", Arrays.asList(
                "rato", "rosa", "rio", "rua", "rede", "raio", "roda", "rei",
                "carro", "cachorro", "terra", "porta", "arte", "forte"
        ));
        infantil.put("L", Arrays.asList(
                "lua", "lobo", "lata", "leão", "lago", "loja", "luva",
                "bola", "cola", "mala", "sala", "vela", "pala"
        ));
        infantil.put("S", Arrays.asList(
                "sapo", "sol", "sino", "saco", "sopa", "sede", "sono",
                "casa", "mesa", "asa", "osso", "isso", "massa"
        ));
        infantil.put("CH", Arrays.asList(
                "chuva", "chave", "chá", "chapéu", "chinelo", "chocolate",
                "tocha", "ficha", "bicho", "macho", "nacho", "acho"
        ));
        infantil.put("LH", Arrays.asList(
                "palha", "malha", "falha", "galho", "milho", "filho",
                "olho", "joelho", "coelho", "orelha", "abelha", "toalha"
        ));

        // FAIXA JUVENIL (7-12 anos)
        Map<String, List<String>> juvenil = new HashMap<>();
        juvenil.put("R", Arrays.asList(
                "revista", "remédio", "robô", "rapido", "relógio", "recreio",
                "narrar", "correr", "morrer", "barreira", "terreno", "arrancar"
        ));
        juvenil.put("L", Arrays.asList(
                "luminoso", "lâmpada", "lápis", "livro", "lugar", "letra",
                "aluno", "clube", "classe", "planta", "bloco", "globo"
        ));
        juvenil.put("S", Arrays.asList(
                "sapato", "serpente", "sistema", "silêncio", "secreto",
                "pessoa", "passar", "professor", "processo", "sucesso"
        ));
        juvenil.put("CH", Arrays.asList(
                "cachorro", "churrasco", "fechadura", "machado", "mochila",
                "farofa", "espaço", "raciocínio", "preenchimento"
        ));
        juvenil.put("LH", Arrays.asList(
                "trabalho", "aparelho", "conselho", "detalhes", "agulha",
                "bilhete", "familia", "batalha", "velhice", "escolha"
        ));

        // FAIXA ADULTA (13+ anos)
        Map<String, List<String>> adulto = new HashMap<>();
        adulto.put("R", Arrays.asList(
                "responsabilidade", "representante", "revolucionário", "refrigerador",
                "extraordinário", "irregularidade", "infraestrutura", "arrependimento"
        ));
        adulto.put("L", Arrays.asList(
                "legislação", "literatura", "luminosidade", "livremente",
                "planetário", "flexibilidade", "plausível", "cláusula"
        ));
        adulto.put("S", Arrays.asList(
                "sustentabilidade", "susceptível", "sensibilidade", "simultâneo",
                "processamento", "acessibilidade", "essencial", "possibilidade"
        ));
        adulto.put("CH", Arrays.asList(
                "chantagem", "chocalho", "charlatão", "chilique", "chaminé",
                "preencher", "enxurrada", "enxaqueca", "enchumaçar"
        ));
        adulto.put("LH", Arrays.asList(
                "acolhimento", "detalhamento", "envelhecimento", "espalhamento",
                "maravilhoso", "conselheiro", "recolhimento", "brilhante"
        ));

        banco.put("infantil", infantil);
        banco.put("juvenil", juvenil);
        banco.put("adulto", adulto);

        return banco;
    }

    /**
     * Palavras genéricas caso não tenha dificuldade específica
     */
    private List<String> palavrasGenericas(String faixaEtaria) {
        switch (faixaEtaria) {
            case "infantil":
                return Arrays.asList("gato", "casa", "bola", "dado", "foto", "mãe", "pai", "vovó");
            case "juvenil":
                return Arrays.asList("escola", "amigo", "computador", "telefone", "bicicleta", "futebol");
            case "adulto":
                return Arrays.asList("tecnologia", "informação", "desenvolvimento", "comunicação", "organização");
            default:
                return Arrays.asList("teste", "palavra", "exemplo");
        }
    }

    /**
     * Retorna todas as dificuldades disponíveis
     */
    public List<String> getDificuldadesDisponiveis() {
        return Arrays.asList("R", "L", "S", "CH", "LH", "GERAL");
    }
}