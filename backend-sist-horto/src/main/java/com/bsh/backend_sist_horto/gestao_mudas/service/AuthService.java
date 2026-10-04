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

        String cpf = formatarCpf(registroRequest.cpf());

        if (cpfJaCadastrado(cpf)) {
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
                    HttpStatus.BAD_REQUEST,
                    "A senha deve ter pelo menos 6 caracteres"
            );
        }

        if(!registroRequest.senha().equals(registroRequest.confirmarSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "As senhas não coincidem");
        }

        Beneficiario beneficiario = new Beneficiario();
        beneficiario.setCpf(cpf);
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
                    HttpStatus.BAD_REQUEST,
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

        String cpf = formatarCpf(request.cpf());
        String login = cpf.replaceAll("\\D", "");

        if (cpfJaCadastrado(cpf)) {
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
        beneficiario.setCpf(cpf);
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

    private String formatarCpf(String cpfInformado) {
        String digitos = cpfInformado.replaceAll("\\D", "");

        if (digitos.length() != 11) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CPF deve conter 11 dígitos"
            );
        }

        return digitos.substring(0, 3) + "."
                + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-"
                + digitos.substring(9);
    }

    private boolean cpfJaCadastrado(String cpfFormatado) {
        return beneficiarioRepository.existsByCpf(cpfFormatado)
                || beneficiarioRepository.existsByCpf(cpfFormatado.replaceAll("\\D", ""));
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