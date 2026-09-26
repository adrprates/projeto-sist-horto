package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class SolicitacaoBeneficiarioResumo {
    private Long id;
    private Integer ano;
    private StatusSolicitacao statusAtual;
    private LocalDate dataSolicitacao;

    public SolicitacaoBeneficiarioResumo(Solicitacao solicitacao) {
        this.id = solicitacao.getId();
        this.ano = solicitacao.getParametroAnual().getAno();
        this.statusAtual = solicitacao.getStatusAtual();
        this.dataSolicitacao = solicitacao.getDataSolicitacao();
    }
}