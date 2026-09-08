package com.bsh.backend_sist_horto.gestao_mudas.security;

import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomBeneficiarioDetailsService implements UserDetailsService {

    private final BeneficiarioRepository beneficiarioRepository;

    public CustomBeneficiarioDetailsService(BeneficiarioRepository beneficiarioRepository) {
        this.beneficiarioRepository = beneficiarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Beneficiario beneficiario = beneficiarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Beneficiário não encontrado: " + username));

        return new CustomBeneficiarioDetails(beneficiario);
    }
}