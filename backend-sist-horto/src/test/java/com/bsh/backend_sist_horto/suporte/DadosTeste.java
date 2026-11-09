package com.bsh.backend_sist_horto.suporte;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fábrica de dados de teste que NÃO acessa o banco: monta objetos e corpos de
 * requisição válidos, que cada teste pode alterar para criar o caso inválido.
 */
public final class DadosTeste {

    public static final String SENHA_PADRAO = "senha123";

    /** Limites padrão usados nos testes (iguais aos de um ano típico do programa). */
    public static final int LIMITE_FRUTIFERAS = 10;
    public static final int LIMITE_OUTRAS = 20;
    public static final int LIMITE_TOTAL = 25;
    public static final int MAX_POR_ESPECIE_FRUTIFERA = 5;
    public static final int MAX_POR_ESPECIE_OUTRAS = 10;

    private static final AtomicInteger SEQUENCIA = new AtomicInteger(1);

    private DadosTeste() {}

    public static int proximoNumero() {
        return SEQUENCIA.getAndIncrement();
    }

    /** CPF único e já formatado (000.000.000-00). */
    public static String cpfUnico() {
        String digitos = String.format("%011d", 10_000_000_000L + proximoNumero());
        return formatarCpf(digitos);
    }

    public static String formatarCpf(String digitos) {
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-" + digitos.substring(9);
    }

    public static String somenteDigitos(String texto) {
        return texto.replaceAll("\\D", "");
    }

    public static int anoAtual() {
        return LocalDate.now().getYear();
    }

    // ------------------------------------------------------------------
    // Corpos de requisição (Map = exatamente o JSON que o frontend envia)
    // ------------------------------------------------------------------

    /** Corpo válido para POST /auth/registro. */
    public static Map<String, Object> registroValido(String login, String cpf) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("cpf", cpf);
        corpo.put("celular", "(34)99999-0000");
        corpo.put("telefone", "(34)3831-0000");
        corpo.put("email", login + "@email.com");
        corpo.put("nome", "Morador " + login);
        corpo.put("endereco", "Rua das Palmeiras, 10 - Patrocínio/MG");
        corpo.put("login", login);
        corpo.put("senha", SENHA_PADRAO);
        corpo.put("confirmarSenha", SENHA_PADRAO);
        return corpo;
    }

    /** Corpo válido para POST /beneficiarios (cadastro assistido). */
    public static Map<String, Object> cadastroAssistidoValido(String cpf) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("cpf", cpf);
        corpo.put("celular", "(34)98888-0000");
        corpo.put("telefone", null);
        corpo.put("email", "assistido" + proximoNumero() + "@email.com");
        corpo.put("nome", "Maria Assistida");
        corpo.put("endereco", "Zona Rural, Sítio Boa Vista");
        return corpo;
    }

    public static Map<String, Object> login(String login, String senha) {
        return Map.of("login", login, "senha", senha);
    }

    public static Map<String, Object> alterarSenha(String atual, String nova, String confirmacao) {
        return Map.of("senhaAtual", atual, "novaSenha", nova, "confirmarNovaSenha", confirmacao);
    }

    public static Map<String, Object> itemRascunho(Long mudaId, int quantidade) {
        return Map.of("mudaId", mudaId, "quantidade", quantidade);
    }

    public static Map<String, Object> etapa(String status) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("status", status);
        return corpo;
    }

    public static Map<String, Object> etapa(String status, String motivo, String descricao, LocalDate dataLimite) {
        Map<String, Object> corpo = etapa(status);
        corpo.put("motivo", motivo);
        corpo.put("descricao", descricao);
        corpo.put("dataLimiteRetirada", dataLimite == null ? null : dataLimite.toString());
        return corpo;
    }

    /** Corpo de proposta de alteração: pares (mudaId, quantidade). */
    public static Map<String, Object> proposta(Object... mudaIdEQuantidade) {
        List<Map<String, Object>> itens = new java.util.ArrayList<>();
        for (int i = 0; i < mudaIdEQuantidade.length; i += 2) {
            itens.add(Map.of("mudaId", mudaIdEQuantidade[i], "quantidade", mudaIdEQuantidade[i + 1]));
        }
        Map<String, Object> corpo = etapa("AGUARDANDO_CONFIRMACAO");
        corpo.put("itensPropostos", itens);
        return corpo;
    }

    // ------------------------------------------------------------------
    // Entidades (ainda não persistidas)
    // ------------------------------------------------------------------

    public static Muda muda(String nomePopular, CategoriaMuda categoria) {
        Muda muda = new Muda();
        muda.setNomesPopulares(new java.util.ArrayList<>(List.of(nomePopular)));
        muda.setCategoria(categoria);
        muda.setFamilia("Família de teste");
        muda.setPossuiFlores(false);
        muda.setPossuiFrutos(false);
        muda.setPerdeMuitasFolhas(false);
        return muda;
    }

    /** Muda com características para os testes de filtro do catálogo. */
    public static Muda muda(CategoriaMuda categoria,
                            boolean possuiFlores,
                            boolean possuiFrutos,
                            boolean perdeMuitasFolhas,
                            String... nomesPopulares) {
        Muda muda = muda(nomesPopulares[0], categoria);
        muda.setNomesPopulares(new java.util.ArrayList<>(List.of(nomesPopulares)));
        muda.setPossuiFlores(possuiFlores);
        muda.setPossuiFrutos(possuiFrutos);
        muda.setPerdeMuitasFolhas(perdeMuitasFolhas);
        return muda;
    }

    public static ParametroAnual parametroPadrao(int ano) {
        return parametro(ano, LIMITE_FRUTIFERAS, LIMITE_OUTRAS, LIMITE_TOTAL,
                MAX_POR_ESPECIE_FRUTIFERA, MAX_POR_ESPECIE_OUTRAS);
    }

    public static ParametroAnual parametro(int ano,
                                           int limiteFrutiferas,
                                           int limiteOutras,
                                           int limiteTotal,
                                           int maxPorEspecieFrutifera,
                                           int maxPorEspecieOutras) {
        ParametroAnual parametro = new ParametroAnual();
        parametro.setAno(ano);
        parametro.setLimiteFrutiferas(limiteFrutiferas);
        parametro.setLimiteOutras(limiteOutras);
        parametro.setLimiteTotalMudas(limiteTotal);
        parametro.setMaxPorEspecieFrutifera(maxPorEspecieFrutifera);
        parametro.setMaxPorEspecieOutras(maxPorEspecieOutras);
        return parametro;
    }

    public static Map<String, Object> parametroJson(int ano, int frutiferas, int outras, int total,
                                                    int maxFrutifera, int maxOutras) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("ano", ano);
        corpo.put("limiteFrutiferas", frutiferas);
        corpo.put("limiteOutras", outras);
        corpo.put("limiteTotalMudas", total);
        corpo.put("maxPorEspecieFrutifera", maxFrutifera);
        corpo.put("maxPorEspecieOutras", maxOutras);
        return corpo;
    }
}
