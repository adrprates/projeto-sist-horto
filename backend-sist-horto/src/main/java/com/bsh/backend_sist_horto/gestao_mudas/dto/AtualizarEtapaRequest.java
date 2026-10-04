package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class AtualizarEtapaRequest {
    private StatusSolicitacao status;
    private String motivo;
    private String descricao;
    private LocalDate dataLimiteRetirada;
    private List<ItemPropostoRequest> itensPropostos = new ArrayList<>();
}
