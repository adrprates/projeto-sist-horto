package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarPerfilRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.PerfilResponse;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final BeneficiarioService beneficiarioService;

    public PerfilController(BeneficiarioService beneficiarioService) {
        this.beneficiarioService = beneficiarioService;
    }

    @GetMapping
    public PerfilResponse buscarPerfil(Authentication authentication) {
        return beneficiarioService.getBeneficiarioPerfil(authentication.getName());
    }

    @PutMapping
    public PerfilResponse atualizarPerfil(
            Authentication authentication,
            @RequestBody AtualizarPerfilRequest request) {

        return beneficiarioService.atualizarPerfil(
                authentication.getName(),
                request
        );
    }
}