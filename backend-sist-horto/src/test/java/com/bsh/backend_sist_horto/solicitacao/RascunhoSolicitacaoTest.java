package com.bsh.backend_sist_horto.solicitacao;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.anoAtual;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.itemRascunho;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Montagem do rascunho e envio da solicitação pelo beneficiário.
 * Limites padrão do ano: frutíferas 10, outras 20, total 25, máx. por espécie 5 (frutífera) / 10 (outras).
 */
@DisplayName("Solicitações: rascunho e envio pelo beneficiário")
class RascunhoSolicitacaoTest extends TesteIntegracaoBase {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    private UsuarioTeste morador;
    private Muda pequi;
    private Muda ipe;

    @BeforeEach
    void preparar() {
        cenario.criarParametroAnoAtual();
        morador = cenario.criarBeneficiario();
        pequi = cenario.criarFrutifera("Pequi", 10);
        ipe = cenario.criarNaoFrutifera("Ipê Amarelo", 30);
    }

    private ResultActions adicionar(UsuarioTeste usuario, Muda muda, int quantidade) throws Exception {
        return postJson("/solicitacoes/rascunho/adicionar", itemRascunho(muda.getId(), quantidade), usuario);
    }

    private Long idDoPrimeiroItem(UsuarioTeste usuario) throws Exception {
        String json = get("/solicitacoes/rascunho", usuario).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(json, "$.itens[0].id")).longValue();
    }

    @Nested
    @DisplayName("Montagem do rascunho")
    class Montagem {

        @Test
        @DisplayName("O rascunho do ano é criado uma única vez, vazio, na primeira consulta")
        void criaRascunho() throws Exception {
            get("/solicitacoes/rascunho", morador)
                    .andExpect(jsonPath("$.statusAtual").value("RASCUNHO"))
                    .andExpect(jsonPath("$.parametroAnual.ano").value(anoAtual()))
                    .andExpect(jsonPath("$.itens", hasSize(0)));
            get("/solicitacoes/rascunho", morador);

            assertThat(solicitacaoRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Adicionar a mesma muda soma no item existente; mudas diferentes viram itens diferentes")
        void adicionaItens() throws Exception {
            adicionar(morador, pequi, 2).andExpect(status().isOk());
            adicionar(morador, pequi, 3)
                    .andExpect(jsonPath("$.itens", hasSize(1)))
                    .andExpect(jsonPath("$.itens[0].quantidade").value(5));
            adicionar(morador, ipe, 4).andExpect(jsonPath("$.itens", hasSize(2)));
        }

        @Test
        @DisplayName("Quantidade acima do disponível (descontando reservas de outros) é recusada")
        void estoqueInsuficiente() throws Exception {
            cenario.criarSolicitacaoPendente(cenario.criarBeneficiario(), item(pequi, 5));
            cenario.criarSolicitacaoPendente(cenario.criarBeneficiario(), item(pequi, 3));

            adicionar(morador, pequi, 3)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "Estoque insuficiente para a muda selecionada. Restam 2 unidades disponíveis."));
            adicionar(morador, pequi, 2).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Ajustes nos itens")
    class Ajustes {

        @Test
        @DisplayName("Altera a quantidade e remove itens")
        void alteraERemove() throws Exception {
            adicionar(morador, ipe, 2);
            Long itemId = idDoPrimeiroItem(morador);

            putJson("/solicitacoes/rascunho/itens/" + itemId, Map.of("quantidade", 7), morador)
                    .andExpect(jsonPath("$.itens[0].quantidade").value(7));
            deletar("/solicitacoes/rascunho/itens/" + itemId, morador)
                    .andExpect(jsonPath("$.itens", hasSize(0)));
        }

        @Test
        @DisplayName("Alteração inválida (zero ou acima do limite) é recusada e a quantidade anterior é mantida")
        void alteracaoInvalida() throws Exception {
            adicionar(morador, ipe, 2);
            Long itemId = idDoPrimeiroItem(morador);

            putJson("/solicitacoes/rascunho/itens/" + itemId, Map.of("quantidade", 0), morador)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("A quantidade deve ser maior que zero."));
            putJson("/solicitacoes/rascunho/itens/" + itemId, Map.of("quantidade", 11), morador)
                    .andExpect(status().isBadRequest());

            get("/solicitacoes/rascunho", morador).andExpect(jsonPath("$.itens[0].quantidade").value(2));
        }

        @Test
        @DisplayName("Não é possível alterar ou remover item do rascunho de outra pessoa")
        void itemDeOutraPessoa() throws Exception {
            UsuarioTeste outro = cenario.criarBeneficiario();
            adicionar(outro, pequi, 2);
            Long itemDoOutro = idDoPrimeiroItem(outro);

            putJson("/solicitacoes/rascunho/itens/" + itemDoOutro, Map.of("quantidade", 1), morador)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Item não encontrado na solicitação."));
            deletar("/solicitacoes/rascunho/itens/" + itemDoOutro, morador).andExpect(status().isBadRequest());

            get("/solicitacoes/rascunho", outro).andExpect(jsonPath("$.itens[0].quantidade").value(2));
        }
    }

    @Nested
    @DisplayName("Envio para análise")
    class Envio {

        @Test
        @DisplayName("Enviar deixa PENDENTE, registra a etapa e reserva as mudas (o físico não muda)")
        void envia() throws Exception {
            Long id = cenario.montarRascunho(morador, item(pequi, 2), item(ipe, 5));

            postSemCorpo("/solicitacoes/" + id + "/enviar", morador)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusAtual").value("PENDENTE"))
                    .andExpect(jsonPath("$.etapas[0].status").value("PENDENTE"))
                    .andExpect(jsonPath("$.etapas[0].descricao", containsString("Recebemos sua solicitação")));

            assertThat(cenario.estoqueFisico(pequi)).isEqualTo(10);
            assertThat(cenario.estoqueReservado(pequi)).isEqualTo(2);
            assertThat(cenario.estoqueDisponivel(pequi)).isEqualTo(8);
        }

        @Test
        @DisplayName("Não envia rascunho vazio nem envia duas vezes")
        void envioInvalido() throws Exception {
            Long vazio = cenario.montarRascunho(morador);
            postSemCorpo("/solicitacoes/" + vazio + "/enviar", morador)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("A solicitação não possui itens."));

            Long id = cenario.montarRascunho(morador, item(pequi, 2));
            postSemCorpo("/solicitacoes/" + id + "/enviar", morador).andExpect(status().isOk());
            postSemCorpo("/solicitacoes/" + id + "/enviar", morador)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("A solicitação já foi enviada."));
            assertThat(cenario.estoqueReservado(pequi)).isEqualTo(2);
        }

        @Test
        @DisplayName("Se o estoque acabou entre a montagem e o envio, o envio é bloqueado")
        void estoqueAcabouAntesDoEnvio() throws Exception {
            Long id = cenario.montarRascunho(morador, item(pequi, 5));
            // outra pessoa envia antes e o viveiro perde mudas: 7 em estoque - 3 reservadas = 4
            cenario.criarSolicitacaoPendente(cenario.criarBeneficiario(), item(pequi, 3));
            cenario.definirEstoque(pequi, 7);

            postSemCorpo("/solicitacoes/" + id + "/enviar", morador)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "Estoque insuficiente para: Pequi (5 solicitadas, 4 disponíveis)"));
            assertThat(cenario.statusDe(id)).isEqualTo(StatusSolicitacao.RASCUNHO);
        }

        @Test
        @DisplayName("Um beneficiário não envia nem vê a solicitação de outra pessoa")
        void solicitacaoAlheia() throws Exception {
            Long idDoOutro = cenario.montarRascunho(cenario.criarBeneficiario(), item(pequi, 2));

            postSemCorpo("/solicitacoes/" + idDoOutro + "/enviar", morador)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Esta solicitação não pertence a você."));
            get("/solicitacoes/minhas-solicitacoes/" + idDoOutro, morador).andExpect(status().isForbidden());

            assertThat(cenario.statusDe(idDoOutro)).isEqualTo(StatusSolicitacao.RASCUNHO);
        }

        @Test
        @DisplayName("Após o envio, o beneficiário acompanha a solicitação e não pode abrir outra no mesmo ano")
        void acompanhamentoEUmaPorAno() throws Exception {
            Long id = cenario.criarSolicitacaoPendente(morador, item(pequi, 2));

            get("/solicitacoes/minha-solicitacao-atual", morador)
                    .andExpect(jsonPath("$.id").value(id))
                    .andExpect(jsonPath("$.statusAtual").value("PENDENTE"));
            get("/solicitacoes/minhas-solicitacoes", morador)
                    .andExpect(jsonPath("$[0].ano").value(anoAtual()));

            adicionar(morador, ipe, 1).andExpect(status().is4xxClientError());
            assertThat(solicitacaoRepository.count()).isEqualTo(1);
        }
    }
}
