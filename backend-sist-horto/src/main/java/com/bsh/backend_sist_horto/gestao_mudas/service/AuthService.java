package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarSenhaRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CadastroAssistidoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.CredenciaisResponse;
import com.bsh.backend_sist_horto.gestao_mudas.record.RegisterRequest;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;

@Service
public class AuthService {

    private static final String CARACTERES_SENHA = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private static final int TAMANHO_SENHA_PROVISORIA = 8;

    private final SecureRandom geradorAleatorio = new SecureRandom();
    private final BeneficiarioRepository beneficiarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(BeneficiarioRepository beneficiarioRepository, PasswordEncoder passwordEncoder) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrar(RegisterRequest registroRequest) {

        if (beneficiarioRepository.existsByCpf(registroRequest.cpf())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "CPF já cadastrado"
            );
        }

        if (beneficiarioRepository.existsByLogin(registroRequest.login())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Login já cadastrado"
            );
        }

        if(registroRequest.senha().length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A senha deve ter pelo menos 6 caracteres"
            );
        }

        if(!registroRequest.senha().equals(registroRequest.confirmarSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "As senhas não coincidem");
        }

        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setCpf(registroRequest.cpf());
        beneficiario.setCelular(registroRequest.celular());
        beneficiario.setTelefone(registroRequest.telefone());
        beneficiario.setEmail(registroRequest.email());
        beneficiario.setNome(registroRequest.nome());
        beneficiario.setEndereco(registroRequest.endereco());
        beneficiario.setLogin(registroRequest.login());
        beneficiario.setSenha(passwordEncoder.encode(registroRequest.senha()));
        beneficiario.setRole(Role.BENEFICIARIO);

        beneficiarioRepository.save(beneficiario);
    }

    public void atualizarSenha(
            Authentication authentication,
            AtualizarSenhaRequest request) {

        Beneficiario beneficiario = beneficiarioRepository
                .findByLogin(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(
                request.senhaAtual(),
                beneficiario.getSenha())) {

            throw new RuntimeException("Senha atual inválida");
        }

        if(request.novaSenha().length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A nova senha deve ter pelo menos 6 caracteres"
            );
        }

        if(!request.novaSenha().equals(request.confirmarNovaSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "As senhas não coincidem");
        }

        beneficiario.setSenha(passwordEncoder.encode(request.novaSenha()));
        beneficiario.setSenhaProvisoria(false);

        beneficiarioRepository.save(beneficiario);
    }

    public CredenciaisResponse cadastrarPeloAdministrador(CadastroAssistidoRequest request) {

        String login = request.cpf().replaceAll("\\D", "");

        if (login.length() != 11) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CPF deve conter 11 dígitos"
            );
        }

        if (beneficiarioRepository.existsByCpf(request.cpf())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "CPF já cadastrado"
            );
        }

        if (beneficiarioRepository.existsByLogin(login)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um usuário com o login " + login
            );
        }

        String senhaGerada = gerarSenhaProvisoria();

        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setCpf(request.cpf());
        beneficiario.setCelular(request.celular());
        beneficiario.setTelefone(request.telefone());
        beneficiario.setEmail(request.email());
        beneficiario.setNome(request.nome());
        beneficiario.setEndereco(request.endereco());
        beneficiario.setLogin(login);
        beneficiario.setSenha(passwordEncoder.encode(senhaGerada));
        beneficiario.setSenhaProvisoria(true);
        beneficiario.setRole(Role.BENEFICIARIO);

        beneficiarioRepository.save(beneficiario);

        return new CredenciaisResponse(
                beneficiario.getId(),
                beneficiario.getNome(),
                login,
                senhaGerada
        );
    }

    public CredenciaisResponse redefinirSenhaProvisoria(Long beneficiarioId) {

        Beneficiario beneficiario = beneficiarioRepository
                .findById(beneficiarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Beneficiário não encontrado"
                ));

        if (beneficiario.getRole() == Role.ADMINISTRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível redefinir a senha de um administrador por aqui"
            );
        }

        String senhaGerada = gerarSenhaProvisoria();

        beneficiario.setSenha(passwordEncoder.encode(senhaGerada));
        beneficiario.setSenhaProvisoria(true);

        beneficiarioRepository.save(beneficiario);

        return new CredenciaisResponse(
                beneficiario.getId(),
                beneficiario.getNome(),
                beneficiario.getLogin(),
                senhaGerada
        );
    }

    public boolean possuiSenhaProvisoria(String login) {
        return beneficiarioRepository
                .findByLogin(login)
                .map(Beneficiario::isSenhaProvisoria)
                .orElse(false);
    }

    private String gerarSenhaProvisoria() {
        StringBuilder senha = new StringBuilder(TAMANHO_SENHA_PROVISORIA);

        for (int i = 0; i < TAMANHO_SENHA_PROVISORIA; i++) {
            senha.append(CARACTERES_SENHA.charAt(
                    geradorAleatorio.nextInt(CARACTERES_SENHA.length())
            ));
        }

        return senha.toString();
    }
}