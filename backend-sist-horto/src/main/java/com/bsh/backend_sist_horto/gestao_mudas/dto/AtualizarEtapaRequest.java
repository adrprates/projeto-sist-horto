package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class AtualizarEtapaRequest {
    private StatusSolicitacao status;
    private String descricao;
    private LocalDate dataLimiteRetirada;
}