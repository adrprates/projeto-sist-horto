package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MudaFilter {

    private String nomePopular;
    private CategoriaMuda categoria;
    private Boolean perdeMuitasFolhas;
    private Boolean possuiFlores;
    private Boolean possuiFrutos;
}