package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter @Setter
public class ItemRelatorioSaida {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataSaida;
    private Long solicitacaoId;
    private String nomeMuda;
    private CategoriaMuda categoria;
    private Integer quantidade;
    private String nomeBeneficiario;
}
