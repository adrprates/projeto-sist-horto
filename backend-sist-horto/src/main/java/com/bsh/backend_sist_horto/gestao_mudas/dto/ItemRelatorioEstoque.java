package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.SituacaoEstoque;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ItemRelatorioEstoque {
    private Long mudaId;
    private String nomeMuda;
    private CategoriaMuda categoria;
    private Integer estoqueFisico;
    private Integer quantidadeReservada;
    private Integer estoqueDisponivel;
    private SituacaoEstoque situacao;
    private String motivoIndisponibilidade;
}
