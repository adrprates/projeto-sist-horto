package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarSenhaRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.LoginRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.RegisterRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.TokenResponse;
import com.bsh.backend_sist_horto.gestao_mudas.security.JwtUtil;
import com.bsh.backend_sist_horto.gestao_mudas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação")
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
    @SecurityRequirements
    @Operation(
            summary = "Realiza o login",
            description = "Valida login e senha e devolve um token JWT. O campo `senhaProvisoria` indica "
                    + "que o usuário foi cadastrado pela Secretaria e precisa trocar a senha.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado, token gerado"),
            @ApiResponse(responseCode = "400", description = "Login ou senha inválidos")
    })
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
    @SecurityRequirements
    @Operation(
            summary = "Registra um novo beneficiário",
            description = "Cria a conta de um morador com perfil BENEFICIARIO. O CPF é aceito com ou sem "
                    + "máscara e armazenado no formato 000.000.000-00.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Beneficiário registrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (campo obrigatório, e-mail, CPF, senha curta ou senhas diferentes)"),
            @ApiResponse(responseCode = "409", description = "CPF ou login já cadastrado")
    })
    public String register(
            @Valid @RequestBody RegisterRequest request) {

        authService.registrar(request);
        return "Beneficiário registrado com sucesso";
    }

    @PutMapping("/atualizar-senha")
    @Operation(summary = "Altera a senha do usuário autenticado",
            description = "Exige a senha atual. Ao alterar, a marcação de senha provisória é removida.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha alterada"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta, nova senha curta ou confirmação diferente"),
            @ApiResponse(responseCode = "403", description = "Usuário não autenticado")
    })
    public String atualizarSenha(
            @Parameter(hidden = true) Authentication authentication,
            @RequestBody AtualizarSenhaRequest request) {

        authService.atualizarSenha(
                authentication,
                request
        );

        return "Senha alterada com sucesso";
    }
}
