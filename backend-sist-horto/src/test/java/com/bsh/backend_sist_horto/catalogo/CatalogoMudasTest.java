package com.bsh.backend_sist_horto.catalogo;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.suporte.DadosTeste;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Catálogo público de mudas. Base usada em todos os testes:
 *
 * Espécie       Categoria     Flores Frutos PerdeFolhas Nomes populares
 * Ipê Amarelo   IPES          sim    não    sim         "Ipê Amarelo", "Ipê do Cerrado"
 * Ipê Roxo      IPES          sim    não    sim         "Ipê Roxo"
 * Pequi         FRUTIFERAS    sim    sim    não         "Pequi", "Piqui"
 * Jabuticaba    FRUTIFERAS    sim    sim    não         "Jabuticaba"
 * Aroeira       MEDICINAIS    não    sim    não         "Aroeira"
 */
@DisplayName("Página de catálogo (GET /mudas)")
class CatalogoMudasTest extends TesteIntegracaoBase {

    private Muda pequi;

    @BeforeEach
    void montarCatalogo() {
        cenario.salvarMuda(DadosTeste.muda(CategoriaMuda.IPES, true, false, true, "Ipê Amarelo", "Ipê do Cerrado"), 20);
        cenario.salvarMuda(DadosTeste.muda(CategoriaMuda.IPES, true, false, true, "Ipê Roxo"), 15);
        pequi = cenario.salvarMuda(DadosTeste.muda(CategoriaMuda.FRUTIFERAS, true, true, false, "Pequi", "Piqui"), 30);
        cenario.salvarMuda(DadosTeste.muda(CategoriaMuda.FRUTIFERAS, true, true, false, "Jabuticaba"), 8);
        cenario.salvarMuda(DadosTeste.muda(CategoriaMuda.MEDICINAIS, false, true, false, "Aroeira"), 0);
    }

    @Test
    @DisplayName("Sem filtros lista todas as mudas, com acesso público")
    void listaTodas() throws Exception {
        get("/mudas", null).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("O disponível desconta só as solicitações enviadas (rascunho não reserva)")
    void descontaReservas() throws Exception {
        cenario.criarParametroAnoAtual();
        cenario.criarSolicitacaoPendente(cenario.criarBeneficiario(), item(pequi, 4));
        cenario.montarRascunho(cenario.criarBeneficiario(), item(pequi, 2));

        get("/mudas?nomePopular=pequi", null)
                .andExpect(jsonPath("$[0].estoqueTotal").value(30))
                .andExpect(jsonPath("$[0].quantidadeReservada").value(4))
                .andExpect(jsonPath("$[0].estoqueDisponivel").value(26));
    }

    @Test
    @DisplayName("Muda indisponível continua no catálogo, sinalizada e com o motivo")
    void mudaIndisponivel() throws Exception {
        cenario.tornarIndisponivel(pequi, "Fora da época de produção");

        get("/mudas?nomePopular=pequi", null)
                .andExpect(jsonPath("$[0].disponivel").value(false))
                .andExpect(jsonPath("$[0].motivoIndisponibilidade").value("Fora da época de produção"));
    }

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource(delimiter = '|', value = {
            //nome: parcial, sem diferenciar maiúsculas, em qualquer um dos nomes e sem duplicar a muda
            "nomePopular=IPÊ                                        | Ipê Amarelo,Ipê Roxo",
            "nomePopular=cerrado                                    | Ipê Amarelo",
            //filtros isolados
            "categoria=FRUTIFERAS                                   | Pequi,Jabuticaba",
            "possuiFlores=false                                     | Aroeira",
            //filtros combinados (E lógico)
            "possuiFrutos=true&categoria=MEDICINAIS                 | Aroeira",
            "nomePopular=ipê&perdeMuitasFolhas=true&possuiFlores=true | Ipê Amarelo,Ipê Roxo",
            "nomePopular=ipê&categoria=FRUTIFERAS                   | ''"
    })
    @DisplayName("Pesquisa por nome e composição de filtros (MudaSpecification)")
    void pesquisa(String consulta, String esperados) throws Exception {
        String json = get("/mudas?" + consulta.trim(), null).andReturn().getResponse().getContentAsString();
        List<String> nomes = JsonPath.read(json, "$[*].nomesPopulares[0]");

        List<String> esperado = esperados.isBlank() ? List.of()
                : Arrays.stream(esperados.split(",")).map(String::trim).toList();
        assertThat(nomes).containsExactlyInAnyOrderElementsOf(esperado);
    }
}
