package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class DistribuicaoFilter {

    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private Integer ano;
    private String nomeBeneficiario;
    private String cpfBeneficiario;
}