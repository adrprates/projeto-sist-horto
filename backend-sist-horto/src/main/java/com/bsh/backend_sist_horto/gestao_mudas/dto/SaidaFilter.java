package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class SaidaFilter {

    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private CategoriaMuda categoria;
    private Long mudaId;
}
