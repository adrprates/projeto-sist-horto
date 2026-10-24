package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ResumoSaidaMuda {
    private Long mudaId;
    private String nomeMuda;
    private CategoriaMuda categoria;
    private Integer quantidade;
    private Integer totalBeneficiarios;
}
