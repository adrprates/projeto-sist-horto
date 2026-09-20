package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarQuantidadeRascunhoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.MontarSolicitacaoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.ParametroAnualDisponivel;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import com.bsh.backend_sist_horto.gestao_mudas.service.SolicitacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitacoes")
@CrossOrigin("*")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final BeneficiarioService beneficiarioService;

    public SolicitacaoController(SolicitacaoService solicitacaoService,
                                 BeneficiarioService beneficiarioService) {
        this.solicitacaoService = solicitacaoService;
        this.beneficiarioService = beneficiarioService;
    }

    @PostMapping("/rascunho/adicionar")
    public Solicitacao montarSolicitacao(
            Authentication authentication,
            @RequestBody MontarSolicitacaoRequest request) {

        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService.montarSolicitacao(
                request.getMudaId(),
                request.getQuantidade(),
                beneficiario
        );
    }

    @PostMapping("/{id}/enviar")
    public Solicitacao enviarSolicitacao(
            @PathVariable Long id) {

        return solicitacaoService.enviarSolicitacao(id);
    }

    @PutMapping("/rascunho/itens/{itemId}")
    public Solicitacao atualizarQuantidadeItem(
            @PathVariable Long itemId,
            @RequestBody AtualizarQuantidadeRascunhoRequest request,
            Authentication authentication) {

        Beneficiario beneficiario =
                beneficiarioService.getBeneficiarioPorLogin(
                        authentication.getName()
                );

        return solicitacaoService.atualizarQuantidadeItem(
                itemId,
                request.getQuantidade(),
                beneficiario
        );
    }

    @DeleteMapping("/rascunho/itens/{itemId}")
    public Solicitacao removerItem(
            @PathVariable Long itemId,
            Authentication authentication) {

        Beneficiario beneficiario =
                beneficiarioService.getBeneficiarioPorLogin(
                        authentication.getName()
                );

        return solicitacaoService.removerItem(
                itemId,
                beneficiario
        );
    }

    @GetMapping("/rascunho/saldo")
    public ParametroAnualDisponivel buscarSaldo(
            Authentication authentication) {

        Beneficiario beneficiario =
                beneficiarioService.getBeneficiarioPorLogin(
                        authentication.getName()
                );

        return solicitacaoService.calcularSaldo(
                beneficiario
        );
    }

    @GetMapping("/rascunho")
    public Solicitacao buscarRascunho(
            Authentication authentication) {

        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService.buscarOuCriarRascunho(
                beneficiario
        );
    }
}