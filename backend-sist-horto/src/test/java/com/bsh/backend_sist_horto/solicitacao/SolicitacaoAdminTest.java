package com.bsh.backend_sist_horto.solicitacao;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.suporte.DadosTeste;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.anoAtual;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.itemRascunho;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Solicitações: gerenciamento pela Secretaria e atendimento presencial")
class SolicitacaoAdminTest extends TesteIntegracaoBase {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    private UsuarioTeste admin;
    private Muda pequi;

    @BeforeEach
    void preparar() {
        cenario.criarParametroAnoAtual();
        admin = cenario.criarAdministrador();
        pequi = cenario.criarFrutifera("Pequi", 100);
    }

    @Nested
    @DisplayName("Listagem com filtros e ordenação")
    class Listagem {

        private Long pendente;
        private Long aprovada;

        @BeforeEach
        void montar() {
            cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("ana", "Ana Ribeiro"),
                    StatusSolicitacao.ENTREGUE, item(pequi, 1));
            cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("bruno", "Bruno Costa"),
                    StatusSolicitacao.PRONTA_PARA_RETIRADA, item(pequi, 1));
            aprovada = cenario.criarSolicitacaoNoStatus(cenario.criarBeneficiario("carla", "Carla Ribeiro"),
                    StatusSolicitacao.APROVADA, item(pequi, 1));
            pendente = cenario.criarSolicitacaoPendente(cenario.criarBeneficiario("davi", "Davi Souza"), item(pequi, 1));
            cenario.montarRascunho(cenario.criarBeneficiario("edu", "Eduardo Rascunho"), item(pequi, 1));
        }

        @Test
        @DisplayName("Sem rascunhos e na ordem de prioridade: pendente, aprovada, pronta, finalizadas")
        void ordemPrioridade() throws Exception {
            get("/solicitacoes", admin)
                    .andExpect(jsonPath("$.totalElementos").value(4))
                    .andExpect(jsonPath("$.conteudo[*].statusAtual",
                            contains("PENDENTE", "APROVADA", "PRONTA_PARA_RETIRADA", "ENTREGUE")));
        }

        @Test
        @DisplayName("Filtra por status, nome (sem diferenciar maiúsculas), CPF, ano e período")
        void filtros() throws Exception {
            String cpfDavi = beneficiarioRepository.findByLogin("davi").orElseThrow().getCpf();
            String hoje = LocalDate.now().toString();

            get("/solicitacoes?statusSolicitacao=APROVADA", admin)
                    .andExpect(jsonPath("$.conteudo[*].id", contains(aprovada.intValue())));
            get("/solicitacoes?nomeBeneficiario=RIBEIRO", admin)
                    .andExpect(jsonPath("$.conteudo[*].beneficiario.nome", containsInAnyOrder("Ana Ribeiro", "Carla Ribeiro")));
            // trecho final do CPF (os CPFs gerados nos testes têm o mesmo começo)
            get("/solicitacoes?cpfBeneficiario=" + cpfDavi.substring(4), admin)
                    .andExpect(jsonPath("$.conteudo[*].id", contains(pendente.intValue())));
            get("/solicitacoes?ano=" + anoAtual() + "&dataInicial=" + hoje + "&dataFinal=" + hoje, admin)
                    .andExpect(jsonPath("$.totalElementos").value(4));
            get("/solicitacoes?dataInicial=" + LocalDate.now().plusDays(1), admin)
                    .andExpect(jsonPath("$.totalElementos").value(0));
        }

        @Test
        @DisplayName("Paginação respeita o tamanho pedido e limita a 50 por página")
        void paginacao() throws Exception {
            get("/solicitacoes?tamanho=3&pagina=1", admin)
                    .andExpect(jsonPath("$.conteudo", hasSize(1)))
                    .andExpect(jsonPath("$.totalPaginas").value(2));
            get("/solicitacoes?tamanho=1000", admin).andExpect(jsonPath("$.tamanho").value(50));
        }
    }

    @Test
    @DisplayName("Atendimento presencial: cadastro assistido, montagem e envio do pedido em nome do beneficiário")
    void atendimentoPresencial() throws Exception {
        String credenciais = postJson("/beneficiarios", DadosTeste.cadastroAssistidoValido(DadosTeste.cpfUnico()), admin)
                .andReturn().getResponse().getContentAsString();
        long beneficiarioId = ((Number) JsonPath.read(credenciais, "$.beneficiarioId")).longValue();
        String base = "/solicitacoes/beneficiarios/" + beneficiarioId;

        get(base + "/atual", admin).andExpect(status().isNoContent());

        postJson(base + "/rascunho/adicionar", itemRascunho(pequi.getId(), DadosTeste.MAX_POR_ESPECIE_FRUTIFERA + 1), admin)
                .andExpect(status().isBadRequest()); // os limites do ano também valem aqui
        postJson(base + "/rascunho/adicionar", itemRascunho(pequi.getId(), 3), admin)
                .andExpect(jsonPath("$.beneficiario.id").value(beneficiarioId))
                .andExpect(jsonPath("$.itens[0].quantidade").value(3));

        postSemCorpo(base + "/enviar", admin)
                .andExpect(jsonPath("$.statusAtual").value("PENDENTE"))
                .andExpect(jsonPath("$.etapas[0].descricao", containsString("registrada pela Secretaria por " + admin.nome())));

        get(base + "/atual", admin).andExpect(jsonPath("$.statusAtual").value("PENDENTE"));
        assertThat(cenario.estoqueReservado(pequi)).isEqualTo(3);
    }
}
