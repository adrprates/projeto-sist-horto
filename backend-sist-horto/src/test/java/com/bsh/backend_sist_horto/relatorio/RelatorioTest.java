package com.bsh.backend_sist_horto.relatorio;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Relatórios conferidos contra um cenário com valores conhecidos:
 *
 * <pre>
 * Muda        Categoria       Estoque inicial  Movimentação                             Situação esperada
 * Pequi       FRUTIFERAS      50               Ana leva 3, Bruno leva 4 (ENTREGUE)      físico 43, DISPONIVEL
 * Ipê Roxo    IPES            40               Ana leva 2 (ENTREGUE); Carla reserva 6   físico 38, reservado 6, disponível 32
 * Aroeira     MEDICINAIS      5                Daniel pede 1 e é REJEITADO              ESTOQUE_BAIXO (≤ 10)
 * Jatobá      REFLORESTAMENTO 0                -                                         ESGOTADA
 * Cedro       ARBORIZACAO     30               marcado como indisponível                 INDISPONIVEL
 * Sibipiruna  ARBORIZACAO     12               Eva reserva 2 → disponível 10            ESTOQUE_BAIXO (limite exato)
 * Mangaba     FRUTIFERAS      20               Fábio leva 5 mas a retirada EXPIRA       físico 20, DISPONIVEL
 * </pre>
 */
@DisplayName("Relatórios: saída de mudas e posição de estoque (/relatorios)")
class RelatorioTest extends TesteIntegracaoBase {

    private UsuarioTeste admin;
    private Muda ipe;
    private Muda sibipiruna;

    @BeforeEach
    void montarCenario() {
        cenario.criarParametroAnoAtual();
        admin = cenario.criarAdministrador();

        Muda pequi = cenario.criarMuda("Pequi", CategoriaMuda.FRUTIFERAS, 50);
        ipe = cenario.criarMuda("Ipê Roxo", CategoriaMuda.IPES, 40);
        Muda aroeira = cenario.criarMuda("Aroeira", CategoriaMuda.MEDICINAIS, 5);
        cenario.criarMuda("Jatobá", CategoriaMuda.REFLORESTAMENTO, 0);
        Muda cedro = cenario.criarMuda("Cedro", CategoriaMuda.ARBORIZACAO, 30);
        sibipiruna = cenario.criarMuda("Sibipiruna", CategoriaMuda.ARBORIZACAO, 12);
        Muda mangaba = cenario.criarMuda("Mangaba", CategoriaMuda.FRUTIFERAS, 20);

        cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("ana", "Ana"),
                StatusSolicitacao.ENTREGUE, item(pequi, 3), item(ipe, 2));
        cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("bruno", "Bruno"),
                StatusSolicitacao.ENTREGUE, item(pequi, 4));
        cenario.criarSolicitacaoPendente(cenario.criarBeneficiario("carla", "Carla"), item(ipe, 6));
        cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("daniel", "Daniel"),
                StatusSolicitacao.REJEITADA, item(aroeira, 1));
        cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("eva", "Eva"),
                StatusSolicitacao.APROVADA, item(sibipiruna, 2));
        cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("fabio", "Fábio"),
                StatusSolicitacao.EXPIRADA, item(mangaba, 5));

        cenario.tornarIndisponivel(cedro, "Em quarentena");
    }

    @Nested
    @DisplayName("Saída de mudas")
    class SaidaDeMudas {

        @Test
        @DisplayName("Considera só as entregas, com totais, resumo por espécie e linhas que somam o total")
        void relatorioCompleto() throws Exception {
            String json = get("/relatorios/saidas", admin)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalMudas").value(9))
                    .andExpect(jsonPath("$.totalRetiradas").value(2))
                    .andExpect(jsonPath("$.totalBeneficiarios").value(2))
                    .andExpect(jsonPath("$.resumo[*].nomeMuda", contains("Pequi", "Ipê Roxo")))
                    .andExpect(jsonPath("$.resumo[0].quantidade").value(7))
                    .andExpect(jsonPath("$.resumo[0].totalBeneficiarios").value(2))
                    .andExpect(jsonPath("$.itens", hasSize(3)))
                    .andReturn().getResponse().getContentAsString();

            List<Integer> quantidades = JsonPath.read(json, "$.itens[*].quantidade");
            assertThat(quantidades.stream().mapToInt(Integer::intValue).sum()).isEqualTo(9);
        }

        @Test
        @DisplayName("Filtra por categoria e por espécie")
        void filtros() throws Exception {
            get("/relatorios/saidas?categoria=FRUTIFERAS", admin)
                    .andExpect(jsonPath("$.totalMudas").value(7))
                    .andExpect(jsonPath("$.resumo[*].nomeMuda", contains("Pequi")));
            get("/relatorios/saidas?mudaId=" + ipe.getId(), admin)
                    .andExpect(jsonPath("$.totalMudas").value(2))
                    .andExpect(jsonPath("$.totalBeneficiarios").value(1));
        }

        @ParameterizedTest(name = "dataInicial={0} dataFinal={1} → {2} mudas")
        @CsvSource({
                "-30, 0,  9",
                "1,   ,   0",
                ",    -1, 0"
        })
        @DisplayName("Filtra por período (dias relativos a hoje; em branco = sem limite)")
        void filtraPeriodo(Integer diasInicio, Integer diasFim, int totalEsperado) throws Exception {
            StringBuilder url = new StringBuilder("/relatorios/saidas?");
            if (diasInicio != null) url.append("dataInicial=").append(LocalDate.now().plusDays(diasInicio)).append('&');
            if (diasFim != null) url.append("dataFinal=").append(LocalDate.now().plusDays(diasFim));

            get(url.toString(), admin).andExpect(jsonPath("$.totalMudas").value(totalEsperado));
        }
    }

    @Nested
    @DisplayName("Posição de estoque")
    class PosicaoDeEstoque {

        private Map<String, Object> linha(String json, String nome) {
            List<Map<String, Object>> linhas = JsonPath.read(json, "$.itens[?(@.nomeMuda == '" + nome + "')]");
            assertThat(linhas).hasSize(1);
            return linhas.get(0);
        }

        @Test
        @DisplayName("Físico, reservado, disponível e situação de cada espécie conferem com as movimentações")
        void quantidadesESituacoes() throws Exception {
            String json = get("/relatorios/estoque", admin).andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            assertThat(linha(json, "Pequi")).containsEntry("estoqueFisico", 43).containsEntry("situacao", "DISPONIVEL");
            assertThat(linha(json, "Ipê Roxo")).containsEntry("estoqueFisico", 38)
                    .containsEntry("quantidadeReservada", 6).containsEntry("estoqueDisponivel", 32);
            assertThat(linha(json, "Mangaba")).containsEntry("estoqueFisico", 20).containsEntry("quantidadeReservada", 0);
            assertThat(linha(json, "Aroeira")).containsEntry("situacao", "ESTOQUE_BAIXO");
            assertThat(linha(json, "Sibipiruna")).containsEntry("estoqueDisponivel", 10).containsEntry("situacao", "ESTOQUE_BAIXO");
            assertThat(linha(json, "Jatobá")).containsEntry("situacao", "ESGOTADA");
            assertThat(linha(json, "Cedro")).containsEntry("situacao", "INDISPONIVEL")
                    .containsEntry("motivoIndisponibilidade", "Em quarentena");
        }

        @Test
        @DisplayName("Estoque baixo vai até 10 disponíveis: com 11 a espécie já é DISPONIVEL")
        void limiteEstoqueBaixo() throws Exception {
            cenario.definirEstoque(sibipiruna, 13); // 13 - 2 reservadas = 11

            String json = get("/relatorios/estoque", admin).andReturn().getResponse().getContentAsString();

            assertThat(linha(json, "Sibipiruna")).containsEntry("situacao", "DISPONIVEL");
        }

        @Test
        @DisplayName("Totais gerais e resumo por categoria somam as espécies")
        void totais() throws Exception {
            // físico: 43 + 38 + 5 + 0 + 30 + 12 + 20 = 148 | reservado: 6 + 2 = 8 | disponível: 140
            String json = get("/relatorios/estoque", admin)
                    .andExpect(jsonPath("$.totalEspecies").value(7))
                    .andExpect(jsonPath("$.estoqueFisico").value(148))
                    .andExpect(jsonPath("$.quantidadeReservada").value(8))
                    .andExpect(jsonPath("$.estoqueDisponivel").value(140))
                    .andReturn().getResponse().getContentAsString();

            List<Map<String, Object>> arborizacao = JsonPath.read(json, "$.categorias[?(@.categoria == 'ARBORIZACAO')]");
            assertThat(arborizacao.get(0))
                    .containsEntry("totalEspecies", 2)
                    .containsEntry("estoqueFisico", 42)
                    .containsEntry("quantidadeReservada", 2);
        }

        @Test
        @DisplayName("Filtra por categoria, por situação e pelos dois combinados")
        void filtros() throws Exception {
            get("/relatorios/estoque?categoria=IPES", admin)
                    .andExpect(jsonPath("$.itens[*].nomeMuda", contains("Ipê Roxo")))
                    .andExpect(jsonPath("$.estoqueFisico").value(38));
            get("/relatorios/estoque?situacao=ESTOQUE_BAIXO", admin)
                    .andExpect(jsonPath("$.itens[*].nomeMuda", contains("Aroeira", "Sibipiruna")));
            get("/relatorios/estoque?categoria=ARBORIZACAO&situacao=ESTOQUE_BAIXO", admin)
                    .andExpect(jsonPath("$.itens[*].nomeMuda", contains("Sibipiruna")));
        }
    }
}
