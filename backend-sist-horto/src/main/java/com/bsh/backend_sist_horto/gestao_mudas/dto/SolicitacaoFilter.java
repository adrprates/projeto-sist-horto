package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class SolicitacaoFilter {

    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private StatusSolicitacao statusSolicitacao;
    private Integer ano;
    private String nomeBeneficiario;
    private String cpfBeneficiario;
}