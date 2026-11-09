package com.bsh.backend_sist_horto.catalogo;

import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Catálogo: ações de estoque do administrador (/estoques)")
class EstoqueAdminTest extends TesteIntegracaoBase {

    private UsuarioTeste admin;
    private Muda pequi;

    @BeforeEach
    void preparar() {
        admin = cenario.criarAdministrador();
        pequi = cenario.criarFrutifera("Pequi", 20);
    }

    private String url(String acao, int quantidade) {
        return "/estoques/" + pequi.getId() + "/" + acao + "?quantidade=" + quantidade;
    }

    @Test
    @DisplayName("Adicionar soma, remover subtrai e atualizar substitui a quantidade")
    void operacoes() throws Exception {
        postSemCorpo(url("adicionar", 15), admin).andExpect(jsonPath("$.quantidade").value(35));
        patchSemCorpo(url("remover", 5), admin).andExpect(status().isNoContent());
        assertThat(cenario.estoqueFisico(pequi)).isEqualTo(30);

        postSemCorpo(url("atualizar", 0), admin).andExpect(jsonPath("$.quantidade").value(0));
    }

    @ParameterizedTest(name = "{0} {1} → \"{2}\"")
    @CsvSource(delimiter = '|', value = {
            "adicionar | 0  | Quantidade deve ser maior que zero",
            "remover   | -3 | Quantidade deve ser maior que zero",
            "remover   | 21 | Quantidade insuficiente em estoque",
            "atualizar | -1 | Quantidade precisa ser igual ou maior que 0"
    })
    @DisplayName("Quantidades inválidas são recusadas e o estoque nunca fica negativo")
    void quantidadeInvalida(String acao, int quantidade, String mensagem) throws Exception {
        var requisicao = acao.equals("remover")
                ? patchSemCorpo(url(acao, quantidade), admin)
                : postSemCorpo(url(acao, quantidade), admin);

        requisicao.andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(mensagem));
        assertThat(cenario.estoqueFisico(pequi)).isEqualTo(20);
    }

    @Test
    @DisplayName("Alterar o estoque físico não mexe nas reservas das solicitações")
    void reservaPreservada() throws Exception {
        cenario.criarParametroAnoAtual();
        cenario.criarSolicitacaoPendente(cenario.criarBeneficiario(), item(pequi, 5));

        postSemCorpo(url("adicionar", 10), admin).andExpect(status().isOk());

        assertThat(cenario.estoqueFisico(pequi)).isEqualTo(30);
        assertThat(cenario.estoqueReservado(pequi)).isEqualTo(5);
        assertThat(cenario.estoqueDisponivel(pequi)).isEqualTo(25);
    }
}
