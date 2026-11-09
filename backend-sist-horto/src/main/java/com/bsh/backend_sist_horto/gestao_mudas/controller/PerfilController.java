package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarPerfilRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.PerfilResponse;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
@Tag(name = "Perfil")
public class PerfilController {

    private final BeneficiarioService beneficiarioService;

    public PerfilController(BeneficiarioService beneficiarioService) {
        this.beneficiarioService = beneficiarioService;
    }

    @GetMapping
    @Operation(summary = "Retorna os dados do usuário autenticado")
    public PerfilResponse buscarPerfil(@Parameter(hidden = true) Authentication authentication) {
        return beneficiarioService.getBeneficiarioPerfil(authentication.getName());
    }

    @PutMapping
    @Operation(summary = "Atualiza os dados de contato do usuário autenticado",
            description = "Permite alterar nome, e-mail, celular, telefone e endereço. CPF e login não são alterados.")
    public PerfilResponse atualizarPerfil(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody AtualizarPerfilRequest request) {

        return beneficiarioService.atualizarPerfil(
                authentication.getName(),
                request
        );
    }
}
