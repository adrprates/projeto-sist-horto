package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.*;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.record.ResponderConfirmacaoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import com.bsh.backend_sist_horto.gestao_mudas.service.EtapaService;
import com.bsh.backend_sist_horto.gestao_mudas.service.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/solicitacoes")
@CrossOrigin("*")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final BeneficiarioService beneficiarioService;
    private final EtapaService etapaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService,
                                 BeneficiarioService beneficiarioService,
                                 EtapaService etapaoService) {
        this.solicitacaoService = solicitacaoService;
        this.beneficiarioService = beneficiarioService;
        this.etapaoService = etapaoService;
    }

    @GetMapping
    public List<Solicitacao> listar(SolicitacaoFilter filtro) {

        List<Solicitacao> resultado =
                solicitacaoService.listarPorFiltro(filtro);

        System.out.println("Quantidade: " + resultado.size());

        for (Solicitacao solicitacao : resultado) {
            System.out.println(
                    solicitacao.getId() +
                            " - " +
                            solicitacao.getDataSolicitacao()
            );
        }

        return resultado;
    }

    @GetMapping("/minhas-solicitacoes")
    public List<SolicitacaoBeneficiarioResumo> listarMinhasSolicitacoes(
            Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService
                .listarSolicitacoesBeneficiario(beneficiario);
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

    @GetMapping("/{id}")
    public Solicitacao buscarPorId(
            @PathVariable Long id
    ) {
        return solicitacaoService
                .getSolicitacaoPorId(id);
    }

    @PostMapping("/{id}/etapas")
    public Solicitacao atualizarEtapa(
            @PathVariable Long id,
            @RequestBody AtualizarEtapaRequest request
    ) {

        return etapaoService.atualizarEtapa(
                id,
                request.getStatus(),
                request.getDescricao(),
                request.getDataLimiteRetirada()
        );
    }

    @GetMapping("/minhas-solicitacoes/{id}")
    public Solicitacao buscarMinhaSolicitacao(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService.getSolicitacaoDoBeneficiario(id, beneficiario);
    }

    @PostMapping("/{id}/confirmar")
    public Solicitacao responderConfirmacao(
            @PathVariable Long id,
            @Valid @RequestBody ResponderConfirmacaoRequest request,
            Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return etapaoService.responderConfirmacao(id, request.aceitar(), beneficiario);
    }

    @GetMapping("/beneficiarios/{beneficiarioId}/atual")
    public ResponseEntity<?> buscarSolicitacaoAtualDoBeneficiario(
            @PathVariable Long beneficiarioId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        Optional<Solicitacao> solicitacao =
                solicitacaoService.buscarSolicitacaoDoAno(beneficiario);

        if (solicitacao.isPresent()) {
            return ResponseEntity.ok(solicitacao.get());
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/beneficiarios/{beneficiarioId}/rascunho")
    public Solicitacao buscarRascunhoDoBeneficiario(
            @PathVariable Long beneficiarioId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.buscarOuCriarRascunho(beneficiario);
    }

    @GetMapping("/beneficiarios/{beneficiarioId}/rascunho/saldo")
    public ParametroAnualDisponivel buscarSaldoDoBeneficiario(
            @PathVariable Long beneficiarioId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.calcularSaldo(beneficiario);
    }

    @PostMapping("/beneficiarios/{beneficiarioId}/rascunho/adicionar")
    public Solicitacao adicionarItemParaBeneficiario(
            @PathVariable Long beneficiarioId,
            @RequestBody MontarSolicitacaoRequest request
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.montarSolicitacao(
                request.getMudaId(),
                request.getQuantidade(),
                beneficiario
        );
    }

    @PutMapping("/beneficiarios/{beneficiarioId}/rascunho/itens/{itemId}")
    public Solicitacao atualizarItemDoBeneficiario(
            @PathVariable Long beneficiarioId,
            @PathVariable Long itemId,
            @RequestBody AtualizarQuantidadeRascunhoRequest request
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.atualizarQuantidadeItem(
                itemId,
                request.getQuantidade(),
                beneficiario
        );
    }

    @DeleteMapping("/beneficiarios/{beneficiarioId}/rascunho/itens/{itemId}")
    public Solicitacao removerItemDoBeneficiario(
            @PathVariable Long beneficiarioId,
            @PathVariable Long itemId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.removerItem(itemId, beneficiario);
    }

    @PostMapping("/beneficiarios/{beneficiarioId}/enviar")
    public Solicitacao enviarSolicitacaoDoBeneficiario(
            @PathVariable Long beneficiarioId,
            Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);
        Beneficiario responsavel = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService.enviarRascunhoDoBeneficiario(
                beneficiario,
                responsavel.getNome()
        );
    }

    @GetMapping("/minha-solicitacao-atual")
    public ResponseEntity<?> buscarSolicitacaoAtual(
            Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        Optional<Solicitacao> solicitacao =
                solicitacaoService.buscarSolicitacaoDoAno(beneficiario);

        if (solicitacao.isPresent()) {
            return ResponseEntity.ok(solicitacao.get());
        }

        return ResponseEntity.noContent().build();
    }
}