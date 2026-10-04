package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.BeneficiarioFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarBeneficiarioRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CadastroAssistidoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CorrigirCpfRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CredenciaisResponse;
import com.bsh.backend_sist_horto.gestao_mudas.service.AuthService;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beneficiarios")
@CrossOrigin("*")
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;
    private final AuthService authService;

    public BeneficiarioController(BeneficiarioService beneficiarioService, AuthService authService) {
        this.beneficiarioService = beneficiarioService;
        this.authService = authService;
    }

    @GetMapping
    public List<Beneficiario> buscarTodos(BeneficiarioFilter beneficiarioFilter) {
        return beneficiarioService.listarPorFiltro(beneficiarioFilter);
    }

    @GetMapping("/{id}")
    public Beneficiario buscarPorId(@PathVariable Long id) {
        return beneficiarioService.getBeneficiarioPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CredenciaisResponse cadastrar(@Valid @RequestBody CadastroAssistidoRequest request) {
        return authService.cadastrarPeloAdministrador(request);
    }

    @PutMapping("/{id}")
    public Beneficiario atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarBeneficiarioRequest request) {
        return beneficiarioService.atualizarPeloAdministrador(id, request);
    }

    @PatchMapping("/{id}/cpf")
    public Beneficiario corrigirCpf(
            @PathVariable Long id,
            @Valid @RequestBody CorrigirCpfRequest request) {
        return beneficiarioService.corrigirCpf(id, request.cpf());
    }

    @PostMapping("/{id}/redefinir-senha")
    public CredenciaisResponse redefinirSenha(@PathVariable Long id) {
        return authService.redefinirSenhaProvisoria(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        Beneficiario beneficiario = buscarPorId(id);
        beneficiarioService.deletar(beneficiario);
    }
}
