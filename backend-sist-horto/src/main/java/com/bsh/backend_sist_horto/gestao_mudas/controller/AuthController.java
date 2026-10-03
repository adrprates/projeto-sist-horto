package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarSenhaRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.LoginRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.RegisterRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.TokenResponse;
import com.bsh.backend_sist_horto.gestao_mudas.security.JwtUtil;
import com.bsh.backend_sist_horto.gestao_mudas.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.login(), loginRequest.senha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        String token = jwtUtil.generateToken(userDetails);
        return new TokenResponse(
                token,
                authService.possuiSenhaProvisoria(userDetails.getUsername())
        );
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(
            @Valid @RequestBody RegisterRequest request) {

        authService.registrar(request);
        return "Beneficiário registrado com sucesso";
    }

    @PutMapping("/atualizar-senha")
    public String atualizarSenha(
            Authentication authentication,
            @RequestBody AtualizarSenhaRequest request) {

        authService.atualizarSenha(
                authentication,
                request
        );

        return "Senha alterada com sucesso";
    }
}