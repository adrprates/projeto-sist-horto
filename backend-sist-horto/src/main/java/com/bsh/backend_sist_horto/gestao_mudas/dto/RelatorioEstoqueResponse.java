package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class RelatorioEstoqueResponse {
    private Integer totalEspecies;
    private Integer estoqueFisico;
    private Integer quantidadeReservada;
    private Integer estoqueDisponivel;
    private List<ResumoEstoqueCategoria> categorias;
    private List<ItemRelatorioEstoque> itens;
}
