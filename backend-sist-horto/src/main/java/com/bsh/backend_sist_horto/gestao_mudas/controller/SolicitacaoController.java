package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.*;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.record.ResponderConfirmacaoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import com.bsh.backend_sist_horto.gestao_mudas.service.EtapaService;
import com.bsh.backend_sist_horto.gestao_mudas.service.SolicitacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/solicitacoes")
@CrossOrigin("*")
public class SolicitacaoController {

    private static final String TAG_BENEFICIARIO = "Solicitações";
    private static final String TAG_ADMIN = "Solicitações (Administração)";

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
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Lista as solicitações enviadas (sem rascunhos)",
            description = "Filtros opcionais por período, status, ano, nome e CPF do beneficiário. Ordem: "
                    + "`prioridade` (padrão: pendentes → aprovadas → prontas → aguardando confirmação → finalizadas), "
                    + "`recentes` ou `antigas`.")
    public PaginaResponse<Solicitacao> listar(
            @ParameterObject SolicitacaoFilter filtro,
            @Parameter(description = "Página (começa em 0)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Itens por página (1 a 50)") @RequestParam(defaultValue = "10") int tamanho,
            @Parameter(description = "prioridade | recentes | antigas") @RequestParam(defaultValue = "prioridade") String ordem) {
        return solicitacaoService.listarPorFiltro(filtro, pagina, tamanho, ordem);
    }

    @GetMapping("/minhas-solicitacoes")
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Histórico de solicitações do usuário autenticado (todos os anos)")
    public List<SolicitacaoBeneficiarioResumo> listarMinhasSolicitacoes(
            @Parameter(hidden = true) Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService
                .listarSolicitacoesBeneficiario(beneficiario);
    }

    @PostMapping("/rascunho/adicionar")
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Adiciona uma muda ao rascunho",
            description = "Cria o rascunho do ano se ainda não existir. Se a muda já estiver no rascunho, a "
                    + "quantidade é somada. Valida limites do parâmetro anual, disponibilidade e estoque.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item adicionado"),
            @ApiResponse(responseCode = "400", description = "Muda indisponível, estoque insuficiente, limite excedido ou parâmetro anual inexistente")
    })
    public Solicitacao montarSolicitacao(
            @Parameter(hidden = true) Authentication authentication,
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
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Envia o rascunho para análise",
            description = "O rascunho passa a PENDENTE e as mudas ficam reservadas no estoque.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitação enviada (PENDENTE)"),
            @ApiResponse(responseCode = "400", description = "Já enviada, sem itens, mudas indisponíveis, sem estoque ou limite excedido"),
            @ApiResponse(responseCode = "403", description = "A solicitação pertence a outro beneficiário")
    })
    public Solicitacao enviarSolicitacao(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication) {

        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        // Garante que o usuário só envia o próprio rascunho (lança 403 caso contrário)
        solicitacaoService.getSolicitacaoDoBeneficiario(id, beneficiario);

        return solicitacaoService.enviarSolicitacao(id);
    }

    @PutMapping("/rascunho/itens/{itemId}")
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Altera a quantidade de um item do rascunho")
    public Solicitacao atualizarQuantidadeItem(
            @PathVariable Long itemId,
            @RequestBody AtualizarQuantidadeRascunhoRequest request,
            @Parameter(hidden = true) Authentication authentication) {

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
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Remove um item do rascunho")
    public Solicitacao removerItem(
            @PathVariable Long itemId,
            @Parameter(hidden = true) Authentication authentication) {

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
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Saldo disponível dentro dos limites do ano",
            description = "Quanto já foi usado e quanto ainda resta de frutíferas, outras espécies e total geral.")
    public ParametroAnualDisponivel buscarSaldo(
            @Parameter(hidden = true) Authentication authentication) {

        Beneficiario beneficiario =
                beneficiarioService.getBeneficiarioPorLogin(
                        authentication.getName()
                );

        return solicitacaoService.calcularSaldo(
                beneficiario
        );
    }

    @GetMapping("/rascunho")
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Retorna (ou cria) o rascunho do usuário autenticado")
    public Solicitacao buscarRascunho(
            @Parameter(hidden = true) Authentication authentication) {

        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService.buscarOuCriarRascunho(
                beneficiario
        );
    }

    @GetMapping("/{id}")
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Detalhes de qualquer solicitação")
    public Solicitacao buscarPorId(
            @PathVariable Long id
    ) {
        return solicitacaoService
                .getSolicitacaoPorId(id);
    }

    @PostMapping("/{id}/etapas")
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Avança a solicitação para uma nova etapa",
            description = """
                    Transições permitidas:
                    - PENDENTE → APROVADA, REJEITADA (exige motivo ou descrição) ou AGUARDANDO_CONFIRMACAO (exige itensPropostos)
                    - APROVADA → PRONTA_PARA_RETIRADA (exige dataLimiteRetirada não passada) ou AGUARDANDO_CONFIRMACAO
                    - AGUARDANDO_CONFIRMACAO → APROVADA ou REJEITADA
                    - PRONTA_PARA_RETIRADA → ENTREGUE (baixa o estoque e registra a saída) ou EXPIRADA (libera a reserva)
                    - ENTREGUE, REJEITADA e EXPIRADA são finais.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Etapa registrada"),
            @ApiResponse(responseCode = "400", description = "Transição inválida ou dados obrigatórios ausentes")
    })
    public Solicitacao atualizarEtapa(
            @PathVariable Long id,
            @RequestBody AtualizarEtapaRequest request
    ) {

        return etapaoService.atualizarEtapa(id, request);
    }

    @GetMapping("/minhas-solicitacoes/{id}")
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Detalhes de uma solicitação do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada"),
            @ApiResponse(responseCode = "403", description = "A solicitação pertence a outro beneficiário")
    })
    public Solicitacao buscarMinhaSolicitacao(
            @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return solicitacaoService.getSolicitacaoDoBeneficiario(id, beneficiario);
    }

    @PostMapping("/{id}/confirmar")
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Aceita ou recusa a proposta de alteração da Secretaria",
            description = "Aceitar aplica os itens propostos e aprova a solicitação; recusar a encerra como REJEITADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta registrada"),
            @ApiResponse(responseCode = "400", description = "A solicitação não está aguardando confirmação"),
            @ApiResponse(responseCode = "403", description = "A solicitação pertence a outro beneficiário")
    })
    public Solicitacao responderConfirmacao(
            @PathVariable Long id,
            @Valid @RequestBody ResponderConfirmacaoRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        Beneficiario beneficiario = beneficiarioService
                .getBeneficiarioPorLogin(authentication.getName());

        return etapaoService.responderConfirmacao(id, request.aceitar(), beneficiario);
    }

    @GetMapping("/beneficiarios/{beneficiarioId}/atual")
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Solicitação do ano atual de um beneficiário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada",
                    content = @Content(schema = @Schema(implementation = Solicitacao.class))),
            @ApiResponse(responseCode = "204", description = "O beneficiário não tem solicitação neste ano")
    })
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
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Atendimento: retorna (ou cria) o rascunho de um beneficiário")
    public Solicitacao buscarRascunhoDoBeneficiario(
            @PathVariable Long beneficiarioId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.buscarOuCriarRascunho(beneficiario);
    }

    @GetMapping("/beneficiarios/{beneficiarioId}/rascunho/saldo")
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Atendimento: saldo do rascunho de um beneficiário")
    public ParametroAnualDisponivel buscarSaldoDoBeneficiario(
            @PathVariable Long beneficiarioId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.calcularSaldo(beneficiario);
    }

    @PostMapping("/beneficiarios/{beneficiarioId}/rascunho/adicionar")
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Atendimento: adiciona uma muda ao rascunho de um beneficiário")
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
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Atendimento: altera a quantidade de um item do rascunho de um beneficiário")
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
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Atendimento: remove um item do rascunho de um beneficiário")
    public Solicitacao removerItemDoBeneficiario(
            @PathVariable Long beneficiarioId,
            @PathVariable Long itemId
    ) {
        Beneficiario beneficiario = beneficiarioService.getBeneficiarioPorId(beneficiarioId);

        return solicitacaoService.removerItem(itemId, beneficiario);
    }

    @PostMapping("/beneficiarios/{beneficiarioId}/enviar")
    @Tag(name = TAG_ADMIN)
    @Operation(summary = "Atendimento: envia o rascunho de um beneficiário para análise",
            description = "A linha do tempo registra o nome do administrador responsável pelo pedido.")
    public Solicitacao enviarSolicitacaoDoBeneficiario(
            @PathVariable Long beneficiarioId,
            @Parameter(hidden = true) Authentication authentication
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
    @Tag(name = TAG_BENEFICIARIO)
    @Operation(summary = "Solicitação do ano atual do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada",
                    content = @Content(schema = @Schema(implementation = Solicitacao.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma solicitação neste ano")
    })
    public ResponseEntity<?> buscarSolicitacaoAtual(
            @Parameter(hidden = true) Authentication authentication
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
