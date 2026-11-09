package com.bsh.backend_sist_horto.parametro;

import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.ParametroAnualRepository;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static com.bsh.backend_sist_horto.suporte.DadosTeste.anoAtual;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.itemRascunho;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.parametroJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Parâmetros anuais (/parametros) e seus efeitos no rascunho")
class ParametroAnualTest extends TesteIntegracaoBase {

    @Autowired
    private ParametroAnualRepository parametroAnualRepository;

    private UsuarioTeste admin;

    @BeforeEach
    void preparar() {
        admin = cenario.criarAdministrador();
    }

    @Nested
    @DisplayName("Cadastro e manutenção")
    class Cadastro {

        @Test
        @DisplayName("Cadastra, lista (mais recente primeiro), atualiza e exclui os parâmetros de um ano")
        void ciclo() throws Exception {
            cenario.criarParametro(2024);
            postJson("/parametros", parametroJson(2025, 10, 20, 25, 5, 10), admin)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.limiteTotalMudas").value(25));

            get("/parametros", admin).andExpect(jsonPath("$[*].ano", contains(2025, 2024)));

            putJson("/parametros/2025", parametroJson(2025, 3, 4, 6, 1, 2), admin)
                    .andExpect(jsonPath("$.limiteTotalMudas").value(6));
            assertThat(parametroAnualRepository.findById(2025).orElseThrow().getLimiteFrutiferas()).isEqualTo(3);

            deletar("/parametros/2025", admin).andExpect(status().isNoContent());
            assertThat(parametroAnualRepository.existsById(2025)).isFalse();
        }

        @Test
        @DisplayName("Limite negativo ou ausente é recusado")
        void limitesInvalidos() throws Exception {
            Map<String, Object> negativo = parametroJson(2031, 10, -1, 25, 5, 10);
            Map<String, Object> ausente = parametroJson(2031, 10, 20, 25, 5, 10);
            ausente.remove("limiteTotalMudas");

            postJson("/parametros", negativo, admin).andExpect(status().isBadRequest());
            postJson("/parametros", ausente, admin).andExpect(status().isBadRequest());

            assertThat(parametroAnualRepository.existsById(2031)).isFalse();
        }

        @Test
        @DisplayName("Atualizar um ano inexistente retorna erro e não cria o ano")
        void atualizaInexistente() throws Exception {
            putJson("/parametros/2040", parametroJson(2040, 3, 4, 6, 1, 2), admin).andExpect(status().isBadRequest());

            assertThat(parametroAnualRepository.existsById(2040)).isFalse();
        }

        @Test
        @DisplayName("Beneficiário consulta os parâmetros, mas não altera")
        void beneficiarioSomenteLeitura() throws Exception {
            UsuarioTeste morador = cenario.criarBeneficiario();

            get("/parametros", morador).andExpect(status().isOk());
            postJson("/parametros", parametroJson(2032, 1, 1, 1, 1, 1), morador).andExpect(status().isForbidden());
        }
    }

    /**
     * Seleção de mudas no rascunho com os limites do ano.
     * Limites: frutíferas 4, outras 6, total 8, máx. por espécie frutífera 3, máx. por espécie outras 4.
     * (Todas as combinações da regra estão no ValidadorLimitesServiceTest; aqui conferimos a integração.)
     */
    @Nested
    @DisplayName("Seleção de mudas no rascunho respeitando os limites do ano")
    class SelecaoNoRascunho {

        private UsuarioTeste morador;
        private Muda pequi;
        private Muda ipe;
        private Muda aroeira;

        @BeforeEach
        void preparar() {
            cenario.criarParametroAnoAtual(4, 6, 8, 3, 4);
            morador = cenario.criarBeneficiario();
            pequi = cenario.criarFrutifera("Pequi", 100);
            ipe = cenario.criarNaoFrutifera("Ipê", 100);
            aroeira = cenario.criarNaoFrutifera("Aroeira", 100);
        }

        private ResultActions adicionar(Muda muda, int quantidade) throws Exception {
            return postJson("/solicitacoes/rascunho/adicionar", itemRascunho(muda.getId(), quantidade), morador);
        }

        @Test
        @DisplayName("No limite é aceito; ao somar a mesma muda acima do limite, é recusado e o rascunho não muda")
        void limitePorEspecie() throws Exception {
            adicionar(pequi, 3).andExpect(status().isOk());

            adicionar(pequi, 1)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            "A espécie 'Pequi' ultrapassa o limite máximo de 3 unidades por espécie frutífera."));

            get("/solicitacoes/rascunho", morador).andExpect(jsonPath("$.itens[0].quantidade").value(3));
        }

        @Test
        @DisplayName("O total geral é limitado mesmo respeitando o limite de cada grupo, e o saldo acompanha")
        void limiteTotalESaldo() throws Exception {
            adicionar(pequi, 3).andExpect(status().isOk());
            adicionar(ipe, 4).andExpect(status().isOk());

            get("/solicitacoes/rascunho/saldo", morador)
                    .andExpect(jsonPath("$.frutiferasDisponiveis").value(1))
                    .andExpect(jsonPath("$.outrasDisponiveis").value(2))
                    .andExpect(jsonPath("$.totalUtilizado").value(7))
                    .andExpect(jsonPath("$.totalDisponivel").value(1));

            adicionar(aroeira, 2)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("total geral de mudas solicitadas (9)")));
        }

        @Test
        @DisplayName("Reduzir os limites do ano passa a valer na próxima alteração do rascunho")
        void limiteAlteradoPeloAdministrador() throws Exception {
            adicionar(pequi, 3).andExpect(status().isOk());

            putJson("/parametros/" + anoAtual(), parametroJson(anoAtual(), 4, 6, 8, 1, 4), admin)
                    .andExpect(status().isOk());

            adicionar(ipe, 1)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("limite máximo de 1")));
        }

        @Test
        @DisplayName("Sem parâmetro cadastrado para o ano atual não é possível montar a solicitação")
        void semParametroDoAno() throws Exception {
            parametroAnualRepository.deleteById(anoAtual());

            adicionar(pequi, 1)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Parâmetro anual não cadastrado para o ano " + anoAtual()));
        }
    }
}
