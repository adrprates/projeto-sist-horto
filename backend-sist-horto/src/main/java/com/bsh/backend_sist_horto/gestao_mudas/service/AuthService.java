package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarCredenciaisRequest;
import com.bsh.backend_sist_horto.gestao_mudas.record.RegisterRequest;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final BeneficiarioRepository beneficiarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(BeneficiarioRepository beneficiarioRepository, PasswordEncoder passwordEncoder) {
        this.beneficiarioRepository = beneficiarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrar(RegisterRequest registroRequest) {

        if (beneficiarioRepository.existsByCpf(registroRequest.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if(beneficiarioRepository.existsByLogin(registroRequest.login())) {
            throw new RuntimeException("Login já cadastrado");
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

    public void atualizarCredenciais(
            Long idBeneficiario,
            AtualizarCredenciaisRequest request) {

        Beneficiario beneficiario = beneficiarioRepository
                .findById(idBeneficiario)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(
                request.senhaAtual(),
                beneficiario.getSenha())) {

            throw new RuntimeException("Senha atual inválida");
        }

        if (!beneficiario.getLogin().equals(request.novoLogin())
                && beneficiarioRepository.existsByLogin(request.novoLogin())) {

            throw new RuntimeException("Novo login já está em uso");
        }

        beneficiario.setLogin(request.novoLogin());

        if (request.novaSenha() != null
                && !request.novaSenha().isBlank()) {

            beneficiario.setSenha(
                    passwordEncoder.encode(request.novaSenha()));
        }

        beneficiarioRepository.save(beneficiario);
    }
}