package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MontarSolicitacaoRequest {
     private Long mudaId;
     private Integer quantidade;
}