package com.bsh.backend_sist_horto.solicitacao;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SaidaRepository;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.etapa;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.proposta;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Todas as etapas do processo de doação após o envio
 * Estoque padrão: Pequi 30, Jabuticaba 30, Ipê 30.
 */
@DisplayName("Solicitações: etapas do processo (análise, proposta, retirada, entrega e expiração)")
class FluxoSolicitacaoTest extends TesteIntegracaoBase {

    @Autowired
    private SaidaRepository saidaRepository;

    private UsuarioTeste admin;
    private UsuarioTeste morador;
    private Muda pequi;
    private Muda jabuticaba;
    private Muda ipe;

    @BeforeEach
    void preparar() {
        cenario.criarParametroAnoAtual();
        admin = cenario.criarAdministrador();
        morador = cenario.criarBeneficiario();
        pequi = cenario.criarFrutifera("Pequi", 30);
        jabuticaba = cenario.criarFrutifera("Jabuticaba", 30);
        ipe = cenario.criarNaoFrutifera("Ipê", 30);
    }

    private ResultActions registrarEtapa(Long id, Map<String, Object> corpo) throws Exception {
        return postJson("/solicitacoes/" + id + "/etapas", corpo, admin);
    }

    private ResultActions responder(Long id, UsuarioTeste usuario, Boolean aceitar) throws Exception {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("aceitar", aceitar);
        return postJson("/solicitacoes/" + id + "/confirmar", corpo, usuario);
    }

    private Long pendente() {
        return cenario.criarSolicitacaoPendente(morador, item(pequi, 4), item(ipe, 5));
    }

    @Nested
    @DisplayName("Análise: aprovar ou rejeitar")
    class Analise {

        @Test
        @DisplayName("Aprovar registra a etapa com a mensagem padrão e mantém a reserva")
        void aprova() throws Exception {
            Long id = pendente();

            registrarEtapa(id, etapa("APROVADA"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusAtual").value("APROVADA"))
                    .andExpect(jsonPath("$.etapas[1].descricao", containsString("sua solicitação foi aprovada")));

            assertThat(cenario.estoqueReservado(pequi)).isEqualTo(4);
        }

        @Test
        @DisplayName("Rejeitar exige motivo; com motivo, encerra e libera a reserva")
        void rejeita() throws Exception {
            Long id = pendente();

            registrarEtapa(id, etapa("REJEITADA", "   ", null, null))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Informe o motivo da rejeição."));

            registrarEtapa(id, etapa("REJEITADA", "Documentação incompleta", null, null))
                    .andExpect(jsonPath("$.statusAtual").value("REJEITADA"))
                    .andExpect(jsonPath("$.etapas[1].motivo").value("Documentação incompleta"));
            assertThat(cenario.estoqueReservado(pequi)).isZero();
        }
    }

    @Nested
    @DisplayName("Proposta de alteração e resposta do beneficiário")
    class PropostaAlteracao {

        @Test
        @DisplayName("Propor deixa aguardando, descreve as mudanças e mantém a reserva dos itens originais")
        void propoe() throws Exception {
            Long id = pendente(); // Pequi 4, Ipê 5

            registrarEtapa(id, proposta(pequi.getId(), 2, jabuticaba.getId(), 3))
                    .andExpect(jsonPath("$.statusAtual").value("AGUARDANDO_CONFIRMACAO"))
                    .andExpect(jsonPath("$.itensPropostos", hasSize(2)))
                    .andExpect(jsonPath("$.etapas[1].descricao", containsString("Pequi: de 4 para 2 unidade(s)")))
                    .andExpect(jsonPath("$.etapas[1].descricao", containsString("Ipê: removida da solicitação")))
                    .andExpect(jsonPath("$.etapas[1].descricao", containsString("Jabuticaba: incluída com 3 unidade(s)")));

            assertThat(cenario.estoqueReservado(pequi)).isEqualTo(4);
            assertThat(cenario.estoqueReservado(jabuticaba)).isZero();
        }

        @Test
        @DisplayName("Proposta inválida é recusada: vazia, igual à atual, muda indisponível, sem estoque ou acima do limite")
        void propostaInvalida() throws Exception {
            Long id = pendente();
            Muda manga = cenario.criarMudaIndisponivel("Manga", CategoriaMuda.FRUTIFERAS, 30, "Praga");
            cenario.definirEstoque(jabuticaba, 2);

            registrarEtapa(id, etapa("AGUARDANDO_CONFIRMACAO")).andExpect(status().isBadRequest());
            registrarEtapa(id, proposta(ipe.getId(), 5, pequi.getId(), 4))
                    .andExpect(jsonPath("$.message").value("A proposta é igual à solicitação atual. Altere ao menos um item."));
            registrarEtapa(id, proposta(manga.getId(), 1))
                    .andExpect(jsonPath("$.message", containsString("Manga está indisponível")));
            registrarEtapa(id, proposta(jabuticaba.getId(), 3))
                    .andExpect(jsonPath("$.message", containsString("Estoque insuficiente para Jabuticaba: restam 2")));
            registrarEtapa(id, proposta(pequi.getId(), 6))
                    .andExpect(jsonPath("$.message", containsString("ultrapassa o limite máximo de 5")));

            assertThat(cenario.statusDe(id)).isEqualTo(StatusSolicitacao.PENDENTE);
        }

        @Test
        @DisplayName("Aceitar aplica os itens propostos, aprova e passa a reservar os novos itens")
        void aceita() throws Exception {
            Long id = pendente();
            cenario.proporAlteracao(id, item(pequi, 2), item(jabuticaba, 3));

            responder(id, morador, true)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusAtual").value("APROVADA"))
                    .andExpect(jsonPath("$.itensPropostos", hasSize(0)))
                    .andExpect(jsonPath("$.itens[*].muda.nomesPopulares[0]", containsInAnyOrder("Pequi", "Jabuticaba")));

            assertThat(cenario.estoqueReservado(pequi)).isEqualTo(2);
            assertThat(cenario.estoqueReservado(jabuticaba)).isEqualTo(3);
            assertThat(cenario.estoqueReservado(ipe)).isZero();
        }

        @Test
        @DisplayName("Recusar encerra como rejeitada e libera todo o estoque")
        void recusa() throws Exception {
            Long id = pendente();
            cenario.proporAlteracao(id, item(pequi, 2));

            responder(id, morador, false)
                    .andExpect(jsonPath("$.statusAtual").value("REJEITADA"))
                    .andExpect(jsonPath("$.etapas[2].motivo").value("Alteração recusada pelo beneficiário"));

            assertThat(cenario.estoqueReservado(pequi)).isZero();
            assertThat(cenario.estoqueReservado(ipe)).isZero();
        }

        @Test
        @DisplayName("Resposta inválida: de outra pessoa, sem informar a escolha ou duas vezes")
        void respostaInvalida() throws Exception {
            Long id = pendente();
            cenario.proporAlteracao(id, item(pequi, 2));

            responder(id, cenario.criarBeneficiario(), true).andExpect(status().isForbidden());
            responder(id, morador, null)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Informe se aceita ou recusa a alteração"));

            responder(id, morador, true).andExpect(status().isOk());
            responder(id, morador, false)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Esta solicitação não está aguardando a sua confirmação."));
            assertThat(cenario.statusDe(id)).isEqualTo(StatusSolicitacao.APROVADA);
        }

        @Test
        @DisplayName("Se o estoque da proposta acabou antes do aceite, o aceite é bloqueado")
        void estoqueAcabouAntesDoAceite() throws Exception {
            Long id = pendente();
            cenario.proporAlteracao(id, item(pequi, 2), item(jabuticaba, 3));
            cenario.definirEstoque(jabuticaba, 1);

            responder(id, morador, true)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("não estão mais disponíveis (Jabuticaba)")));
            assertThat(cenario.statusDe(id)).isEqualTo(StatusSolicitacao.AGUARDANDO_CONFIRMACAO);
        }
    }

    @Nested
    @DisplayName("Retirada: data limite, entrega e expiração")
    class Retirada {

        @Test
        @DisplayName("Pronta para retirada exige data limite que não esteja no passado")
        void dataLimite() throws Exception {
            Long id = cenario.criarSolicitacaoNoStatus(morador, StatusSolicitacao.APROVADA, item(pequi, 4));
            LocalDate limite = LocalDate.now().plusDays(10);

            registrarEtapa(id, etapa("PRONTA_PARA_RETIRADA"))
                    .andExpect(jsonPath("$.message").value("Informe a data limite de retirada."));
            registrarEtapa(id, etapa("PRONTA_PARA_RETIRADA", null, null, LocalDate.now().minusDays(1)))
                    .andExpect(jsonPath("$.message").value("A data limite de retirada não pode estar no passado."));

            registrarEtapa(id, etapa("PRONTA_PARA_RETIRADA", null, null, limite))
                    .andExpect(jsonPath("$.statusAtual").value("PRONTA_PARA_RETIRADA"))
                    .andExpect(jsonPath("$.etapas[2].dataLimiteRetirada")
                            .value(limite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        }

        @Test
        @DisplayName("Entrega baixa o estoque físico, encerra a reserva, registra a saída e completa a linha do tempo")
        void entrega() throws Exception {
            Long id = cenario.criarSolicitacaoNoStatus(morador, StatusSolicitacao.PRONTA_PARA_RETIRADA,
                    item(pequi, 4), item(ipe, 5));

            registrarEtapa(id, etapa("ENTREGUE"))
                    .andExpect(jsonPath("$.statusAtual").value("ENTREGUE"))
                    .andExpect(jsonPath("$.etapas[*].status",
                            contains("PENDENTE", "APROVADA", "PRONTA_PARA_RETIRADA", "ENTREGUE")));

            assertThat(cenario.estoqueFisico(pequi)).isEqualTo(26);
            assertThat(cenario.estoqueFisico(ipe)).isEqualTo(25);
            assertThat(cenario.estoqueReservado(pequi)).isZero();
            assertThat(saidaRepository.existsBySolicitacaoId(id)).isTrue();
        }

        @Test
        @DisplayName("Expiração devolve as mudas ao disponível sem tirar nada do estoque físico")
        void expira() throws Exception {
            Long id = cenario.criarSolicitacaoNoStatus(morador, StatusSolicitacao.PRONTA_PARA_RETIRADA, item(pequi, 4));

            registrarEtapa(id, etapa("EXPIRADA")).andExpect(jsonPath("$.statusAtual").value("EXPIRADA"));

            assertThat(cenario.estoqueFisico(pequi)).isEqualTo(30);
            assertThat(cenario.estoqueDisponivel(pequi)).isEqualTo(30);
            assertThat(saidaRepository.existsBySolicitacaoId(id)).isFalse();
        }

        @Test
        @DisplayName("Se o estoque físico não cobre a entrega, ela é bloqueada e nada é baixado")
        void entregaSemEstoqueFisico() throws Exception {
            Long id = cenario.criarSolicitacaoNoStatus(morador, StatusSolicitacao.PRONTA_PARA_RETIRADA,
                    item(ipe, 5), item(pequi, 4));
            cenario.definirEstoque(pequi, 3); // perda no viveiro após a separação

            registrarEtapa(id, etapa("ENTREGUE")).andExpect(status().isBadRequest());

            assertThat(cenario.statusDe(id)).isEqualTo(StatusSolicitacao.PRONTA_PARA_RETIRADA);
            assertThat(cenario.estoqueFisico(ipe)).isEqualTo(30);
            assertThat(saidaRepository.existsBySolicitacaoId(id)).isFalse();
        }
    }

    @Nested
    @DisplayName("Transições inválidas")
    class TransicoesInvalidas {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "PENDENTE, ENTREGUE",
                "APROVADA, REJEITADA",
                "PRONTA_PARA_RETIRADA, APROVADA"
        })
        @DisplayName("Pular etapas ou voltar no fluxo é recusado e o status não muda")
        void foraDoFluxo(StatusSolicitacao atual, StatusSolicitacao destino) throws Exception {
            Long id = cenario.criarSolicitacaoNoStatus(morador, atual, item(pequi, 2));

            registrarEtapa(id, etapa(destino.name(), "motivo", null, null)).andExpect(status().isBadRequest());

            assertThat(cenario.statusDe(id)).isEqualTo(atual);
        }

        @ParameterizedTest(name = "{0} é final")
        @EnumSource(value = StatusSolicitacao.class, names = {"ENTREGUE", "REJEITADA", "EXPIRADA"})
        @DisplayName("Solicitações finalizadas não avançam mais (a entrega não baixa o estoque duas vezes)")
        void finalizadas(StatusSolicitacao finalizado) throws Exception {
            Long id = cenario.criarSolicitacaoNoStatus(morador, finalizado, item(pequi, 4));
            int estoqueAntes = cenario.estoqueFisico(pequi);

            registrarEtapa(id, etapa("ENTREGUE"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Esta solicitação já foi finalizada."));

            assertThat(cenario.statusDe(id)).isEqualTo(finalizado);
            assertThat(cenario.estoqueFisico(pequi)).isEqualTo(estoqueAntes);
        }

        @Test
        @DisplayName("Beneficiário não pode registrar etapas, nem na própria solicitação")
        void beneficiarioNaoAprova() throws Exception {
            Long id = pendente();

            postJson("/solicitacoes/" + id + "/etapas", etapa("APROVADA"), morador).andExpect(status().isForbidden());

            assertThat(cenario.statusDe(id)).isEqualTo(StatusSolicitacao.PENDENTE);
        }
    }
}
