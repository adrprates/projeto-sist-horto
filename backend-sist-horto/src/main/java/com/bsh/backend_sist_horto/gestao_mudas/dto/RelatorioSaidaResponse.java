package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class RelatorioSaidaResponse {
    private Integer totalMudas;
    private Integer totalRetiradas;
    private Integer totalBeneficiarios;
    private List<ResumoSaidaMuda> resumo;
    private List<ItemRelatorioSaida> itens;
}
