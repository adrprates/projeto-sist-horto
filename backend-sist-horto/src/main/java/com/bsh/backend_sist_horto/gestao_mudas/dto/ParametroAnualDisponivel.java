package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ParametroAnualDisponivel {

    private Integer frutiferasUtilizadas;
    private Integer frutiferasDisponiveis;
    private Integer outrasUtilizadas;
    private Integer outrasDisponiveis;
    private Integer totalUtilizado;
    private Integer totalDisponivel;
}