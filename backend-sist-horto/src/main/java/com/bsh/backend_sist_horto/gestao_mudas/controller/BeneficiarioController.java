package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.BeneficiarioFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.PaginaResponse;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarBeneficiarioRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CadastroAssistidoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CorrigirCpfRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CredenciaisResponse;
import com.bsh.backend_sist_horto.gestao_mudas.service.AuthService;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/beneficiarios")
@CrossOrigin("*")
@Tag(name = "Beneficiários")
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;
    private final AuthService authService;

    public BeneficiarioController(BeneficiarioService beneficiarioService, AuthService authService) {
        this.beneficiarioService = beneficiarioService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Lista beneficiários com filtros e paginação",
            description = "Filtros opcionais por CPF, nome, e-mail e perfil. Ordenado por nome. Tamanho máximo da página: 50.")
    public PaginaResponse<Beneficiario> buscarTodos(
            @ParameterObject BeneficiarioFilter beneficiarioFilter,
            @Parameter(description = "Página (começa em 0)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Itens por página (1 a 50)") @RequestParam(defaultValue = "10") int tamanho) {
        return beneficiarioService.listarPorFiltro(beneficiarioFilter, pagina, tamanho);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um beneficiário pelo id")
    public Beneficiario buscarPorId(@PathVariable Long id) {
        return beneficiarioService.getBeneficiarioPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastro assistido de beneficiário",
            description = "Usado pela Secretaria para quem não consegue se registrar sozinho. O sistema gera um "
                    + "login (horto#####) e uma senha provisória, devolvidos apenas nesta resposta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiário cadastrado; credenciais retornadas"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado")
    })
    public CredenciaisResponse cadastrar(@Valid @RequestBody CadastroAssistidoRequest request) {
        return authService.cadastrarPeloAdministrador(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de contato de um beneficiário")
    public Beneficiario atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarBeneficiarioRequest request) {
        return beneficiarioService.atualizarPeloAdministrador(id, request);
    }

    @PatchMapping("/{id}/cpf")
    @Operation(summary = "Corrige o CPF de um beneficiário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CPF corrigido"),
            @ApiResponse(responseCode = "400", description = "CPF inválido ou igual ao atual"),
            @ApiResponse(responseCode = "409", description = "CPF já pertence a outro beneficiário")
    })
    public Beneficiario corrigirCpf(
            @PathVariable Long id,
            @Valid @RequestBody CorrigirCpfRequest request) {
        return beneficiarioService.corrigirCpf(id, request.cpf());
    }

    @PostMapping("/{id}/redefinir-senha")
    @Operation(summary = "Gera uma nova senha provisória para o beneficiário",
            description = "Não pode ser usado para administradores.")
    public CredenciaisResponse redefinirSenha(@PathVariable Long id) {
        return authService.redefinirSenhaProvisoria(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um beneficiário")
    public void deletar(@PathVariable Long id) {
        Beneficiario beneficiario = buscarPorId(id);
        beneficiarioService.deletar(beneficiario);
    }
}
