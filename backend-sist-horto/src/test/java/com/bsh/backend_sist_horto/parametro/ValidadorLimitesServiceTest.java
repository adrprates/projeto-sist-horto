package com.bsh.backend_sist_horto.parametro;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import com.bsh.backend_sist_horto.gestao_mudas.service.ValidadorLimitesService;
import com.bsh.backend_sist_horto.suporte.DadosTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Teste unitário (sem Spring e sem banco) da regra de limites anuais.
 * Limites padrão: frutíferas 10, outras 20, total 25, máx. por espécie frutífera 5, outras 10.
 */
@DisplayName("Regra de limites do parâmetro anual (ValidadorLimitesService)")
class ValidadorLimitesServiceTest {

    private final ValidadorLimitesService validador = new ValidadorLimitesService();
    private final ParametroAnual parametro = DadosTeste.parametroPadrao(2026);
    private long proximoId = 1;

    private ItemSolicitacao frutifera(String nome, int quantidade) {
        return item(nome, CategoriaMuda.FRUTIFERAS, quantidade);
    }

    private ItemSolicitacao outra(String nome, int quantidade) {
        return item(nome, CategoriaMuda.IPES, quantidade);
    }

    private ItemSolicitacao item(String nome, CategoriaMuda categoria, int quantidade) {
        Muda muda = DadosTeste.muda(nome, categoria);
        muda.setId(proximoId++);
        ItemSolicitacao item = new ItemSolicitacao();
        item.setMuda(muda);
        item.setQuantidade(quantidade);
        return item;
    }

    @Test
    @DisplayName("Todos os limites atingidos exatamente é válido")
    void exatamenteNosLimites() {
        List<ItemSolicitacao> itens = List.of(
                frutifera("Pequi", 5), frutifera("Jabuticaba", 5),   // frutíferas = 10
                outra("Ipê Amarelo", 10), outra("Ipê Roxo", 5));      // outras = 15, total = 25

        assertThatCode(() -> validador.validar(parametro, itens)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Espécie acima do máximo por espécie (frutífera e demais)")
    void acimaPorEspecie() {
        assertThatThrownBy(() -> validador.validar(parametro, List.of(frutifera("Pequi", 6))))
                .hasMessage("A espécie 'Pequi' ultrapassa o limite máximo de 5 unidades por espécie frutífera.");
        assertThatThrownBy(() -> validador.validar(parametro, List.of(outra("Ipê", 11))))
                .hasMessage("A espécie 'Ipê' ultrapassa o limite máximo de 10 unidades por espécie.");
    }

    @Test
    @DisplayName("A mesma espécie repetida em dois itens soma para o limite por espécie")
    void mesmaEspecieEmDoisItens() {
        ItemSolicitacao primeiro = frutifera("Pequi", 3);
        ItemSolicitacao segundo = new ItemSolicitacao();
        segundo.setMuda(primeiro.getMuda());
        segundo.setQuantidade(3);

        assertThatThrownBy(() -> validador.validar(parametro, List.of(primeiro, segundo)))
                .hasMessageContaining("ultrapassa o limite máximo de 5");
    }

    @Test
    @DisplayName("Total de cada grupo acima do limite, mesmo com cada espécie dentro do máximo")
    void totalPorGrupo() {
        assertThatThrownBy(() -> validador.validar(parametro, List.of(
                frutifera("Pequi", 5), frutifera("Jabuticaba", 5), frutifera("Manga", 1))))
                .hasMessage("O total de mudas frutíferas solicitadas (11) excede o limite anual permitido (10).");
        assertThatThrownBy(() -> validador.validar(parametro, List.of(
                outra("Ipê Amarelo", 10), outra("Ipê Roxo", 10), outra("Aroeira", 1))))
                .hasMessage("O total de outras mudas solicitadas (21) excede o limite anual permitido (20).");
    }

    @Test
    @DisplayName("Total geral acima do limite, mesmo com cada grupo dentro do próprio limite")
    void totalGeral() {
        assertThatThrownBy(() -> validador.validar(parametro, List.of(
                frutifera("Pequi", 5), frutifera("Jabuticaba", 5), outra("Ipê Amarelo", 10), outra("Ipê Roxo", 6))))
                .hasMessage("O total geral de mudas solicitadas (26) excede o limite global do programa (25).");
    }

    @Test
    @DisplayName("Sem parâmetro anual a validação falha")
    void semParametro() {
        assertThatThrownBy(() -> validador.validar(null, List.of(frutifera("Pequi", 1))))
                .hasMessage("Parâmetro anual não definido para esta solicitação.");
    }
}
