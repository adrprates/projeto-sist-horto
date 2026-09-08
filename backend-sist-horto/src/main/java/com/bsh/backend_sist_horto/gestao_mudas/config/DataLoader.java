package com.bsh.backend_sist_horto.gestao_mudas.config;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final BeneficiarioRepository beneficiarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            BeneficiarioRepository beneficiarioRepository,
            PasswordEncoder passwordEncoder) {

        this.beneficiarioRepository = beneficiarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (!beneficiarioRepository.existsByLogin("admin")) {

            Beneficiario admin = new Beneficiario();
            admin.setCpf("123.456.789-00");
            admin.setCelular("(34)99999-1111");
            admin.setTelefone("(34)3842-1111");
            admin.setEmail("admin@sisthorto.com");
            admin.setNome("Administrador do Sistema");
            admin.setEndereco("Rua Administração, 100");
            admin.setLogin("admin");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMINISTRADOR);

            beneficiarioRepository.save(admin);
        }

        if (!beneficiarioRepository.existsByLogin("joao")) {

            Beneficiario beneficiario = new Beneficiario();
            beneficiario.setCpf("987.654.321-00");
            beneficiario.setCelular("(34)99999-2222");
            beneficiario.setTelefone("(34)3842-2222");
            beneficiario.setEmail("beneficiario@sisthorto.com");
            beneficiario.setNome("João da Silva");
            beneficiario.setEndereco("Rua das Mudas, 200");
            beneficiario.setLogin("joao");
            beneficiario.setSenha(passwordEncoder.encode("benef123"));
            beneficiario.setRole(Role.BENEFICIARIO);

            beneficiarioRepository.save(beneficiario);
        }
    }
}