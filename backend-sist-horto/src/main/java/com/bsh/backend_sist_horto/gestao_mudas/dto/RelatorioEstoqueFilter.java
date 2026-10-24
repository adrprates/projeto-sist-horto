package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.SituacaoEstoque;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RelatorioEstoqueFilter {

    private CategoriaMuda categoria;
    private SituacaoEstoque situacao;
}
