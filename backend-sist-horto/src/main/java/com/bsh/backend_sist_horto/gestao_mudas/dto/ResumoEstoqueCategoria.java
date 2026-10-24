package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ResumoEstoqueCategoria {
    private CategoriaMuda categoria;
    private Integer totalEspecies;
    private Integer estoqueFisico;
    private Integer quantidadeReservada;
    private Integer estoqueDisponivel;
}
