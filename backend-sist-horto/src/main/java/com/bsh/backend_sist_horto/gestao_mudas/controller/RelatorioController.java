package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioEstoqueFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioEstoqueResponse;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioSaidaResponse;
import com.bsh.backend_sist_horto.gestao_mudas.dto.SaidaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.service.RelatorioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin("*")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/saidas")
    public RelatorioSaidaResponse relatorioSaidas(SaidaFilter saidaFilter) {
        return relatorioService.gerarRelatorioSaidas(saidaFilter);
    }

    @GetMapping("/estoque")
    public RelatorioEstoqueResponse relatorioEstoque(RelatorioEstoqueFilter relatorioEstoqueFilter) {
        return relatorioService.gerarRelatorioEstoque(relatorioEstoqueFilter);
    }
}
