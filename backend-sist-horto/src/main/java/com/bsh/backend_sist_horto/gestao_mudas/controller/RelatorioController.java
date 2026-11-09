package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioEstoqueFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioEstoqueResponse;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioSaidaResponse;
import com.bsh.backend_sist_horto.gestao_mudas.dto.SaidaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin("*")
@Tag(name = "Relatórios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/saidas")
    @Operation(summary = "Relatório de saída de mudas",
            description = "Mudas efetivamente entregues, com filtros opcionais por período (datas no formato "
                    + "yyyy-MM-dd), categoria e espécie. Traz totais, resumo por espécie e a lista de itens.")
    public RelatorioSaidaResponse relatorioSaidas(@ParameterObject SaidaFilter saidaFilter) {
        return relatorioService.gerarRelatorioSaidas(saidaFilter);
    }

    @GetMapping("/estoque")
    @Operation(summary = "Relatório de posição de estoque",
            description = "Estoque físico, reservado e disponível de cada espécie, com a situação "
                    + "(DISPONIVEL, ESTOQUE_BAIXO até 10 unidades, ESGOTADA ou INDISPONIVEL) e resumo por categoria.")
    public RelatorioEstoqueResponse relatorioEstoque(@ParameterObject RelatorioEstoqueFilter relatorioEstoqueFilter) {
        return relatorioService.gerarRelatorioEstoque(relatorioEstoqueFilter);
    }
}
