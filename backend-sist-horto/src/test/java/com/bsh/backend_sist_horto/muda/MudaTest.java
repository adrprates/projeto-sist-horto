package com.bsh.backend_sist_horto.muda;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.MudaRepository;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.bsh.backend_sist_horto.suporte.CenarioTeste.item;
import static com.bsh.backend_sist_horto.suporte.DadosTeste.itemRascunho;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Mudas: cadastro, edição, disponibilidade e exclusão (/mudas)")
class MudaTest extends TesteIntegracaoBase {

    @Autowired
    private MudaRepository mudaRepository;

    private UsuarioTeste admin;

    @BeforeEach
    void preparar() {
        admin = cenario.criarAdministrador();
    }

    private static Map<String, Object> mudaJson(String nome, String categoria) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nomesPopulares", List.of(nome));
        corpo.put("categoria", categoria);
        corpo.put("familia", "Caryocaraceae");
        corpo.put("possuiFrutos", true);
        return corpo;
    }

    private static Map<String, Object> disponibilidade(Boolean disponivel, String motivo) {
        Map<String, Object> corpo = new HashMap<>();
        corpo.put("disponivel", disponivel);
        corpo.put("motivo", motivo);
        return corpo;
    }

    @Test
    @DisplayName("Cadastra a muda disponível e com estoque zerado")
    void cadastra() throws Exception {
        String resposta = postJson("/mudas", mudaJson("Pequi", "FRUTIFERAS"), admin)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomesPopulares[0]").value("Pequi"))
                .andExpect(jsonPath("$.familia").value("Caryocaraceae"))
                .andExpect(jsonPath("$.disponivel").value(true))
                .andReturn().getResponse().getContentAsString();

        Integer id = JsonPath.read(resposta, "$.id");
        get("/estoques/" + id, admin).andExpect(jsonPath("$.quantidade").value(0));
    }

    @Test
    @DisplayName("Categoria ausente ou inexistente é recusada")
    void categoriaInvalida() throws Exception {
        postJson("/mudas", mudaJson("Pequi", null), admin).andExpect(status().isBadRequest());
        postJson("/mudas", mudaJson("Pequi", "CACTOS"), admin).andExpect(status().isBadRequest());

        assertThat(mudaRepository.count()).isZero();
    }

    @Test
    @DisplayName("Editar altera a muda da URL e preserva estoque e disponibilidade")
    void edita() throws Exception {
        Muda muda = cenario.criarFrutifera("Pequi", 15);
        cenario.tornarIndisponivel(muda, "Em tratamento");

        putJson("/mudas/" + muda.getId(), mudaJson("Pequi Editado", "FRUTIFERAS"), admin)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomesPopulares[0]").value("Pequi Editado"))
                .andExpect(jsonPath("$.disponivel").value(false));

        assertThat(cenario.estoqueFisico(muda)).isEqualTo(15);
        assertThat(mudaRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Editar muda inexistente retorna erro e não cria nada")
    void editaInexistente() throws Exception {
        putJson("/mudas/99999", mudaJson("Fantasma", "FRUTIFERAS"), admin).andExpect(status().isBadRequest());

        assertThat(mudaRepository.count()).isZero();
    }

    @Test
    @DisplayName("Disponibilidade: indisponível guarda o motivo e voltar a ficar disponível o apaga")
    void alternaDisponibilidade() throws Exception {
        Muda muda = cenario.criarFrutifera("Pequi", 10);
        String url = "/mudas/" + muda.getId() + "/disponibilidade";

        patchJson(url, disponibilidade(false, "  Praga no viveiro  "), admin)
                .andExpect(jsonPath("$.disponivel").value(false))
                .andExpect(jsonPath("$.motivoIndisponibilidade").value("Praga no viveiro"));
        patchJson(url, disponibilidade(true, "ignorado"), admin)
                .andExpect(jsonPath("$.disponivel").value(true))
                .andExpect(jsonPath("$.motivoIndisponibilidade").doesNotExist());
    }

    @Test
    @DisplayName("Disponibilidade sem o campo 'disponivel' ou com motivo acima de 255 caracteres é recusada")
    void disponibilidadeInvalida() throws Exception {
        String url = "/mudas/" + cenario.criarFrutifera("Pequi", 10).getId() + "/disponibilidade";

        patchJson(url, disponibilidade(null, "x"), admin).andExpect(status().isBadRequest());
        patchJson(url, disponibilidade(false, "x".repeat(256)), admin).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Muda indisponível não entra no rascunho, e se ficar indisponível depois, bloqueia o envio")
    void indisponivelNaSolicitacao() throws Exception {
        cenario.criarParametroAnoAtual();
        UsuarioTeste morador = cenario.criarBeneficiario();
        Muda pequi = cenario.criarFrutifera("Pequi", 50);
        Muda manga = cenario.criarMudaIndisponivel("Manga", CategoriaMuda.FRUTIFERAS, 50, "Praga");

        postJson("/solicitacoes/rascunho/adicionar", itemRascunho(manga.getId(), 1), morador)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A muda Manga está indisponível no momento."));

        Long rascunho = cenario.montarRascunho(morador, item(pequi, 2));
        cenario.tornarIndisponivel(pequi, "Praga");

        postSemCorpo("/solicitacoes/" + rascunho + "/enviar", morador)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Remova da solicitação as mudas indisponíveis")));
    }

    @Test
    @DisplayName("Exclui a muda e o estoque, mas não exclui muda que faz parte de uma solicitação")
    void exclusao() throws Exception {
        Muda livre = cenario.criarFrutifera("Pequi", 10);
        deletar("/mudas/" + livre.getId(), admin).andExpect(status().isNoContent());
        get("/mudas/" + livre.getId(), null).andExpect(status().isBadRequest());
        get("/estoques/" + livre.getId(), admin).andExpect(status().isBadRequest());

        cenario.criarParametroAnoAtual();
        Muda emUso = cenario.criarFrutifera("Jabuticaba", 10);
        cenario.criarSolicitacaoPendente(cenario.criarBeneficiario(), item(emUso, 2));
        deletar("/mudas/" + emUso.getId(), admin).andExpect(status().is4xxClientError());
        assertThat(mudaRepository.existsById(emUso.getId())).isTrue();
    }
}
