package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class DadosMudaResumo {
    private Long id;
    private List<String> nomesPopulares;
    private CategoriaMuda categoria;
    private String familia;
    private String linkImagemArvore;
    private Integer estoqueDisponivel;
}