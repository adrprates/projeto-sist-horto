package com.bsh.backend_sist_horto.gestao_mudas.security;

import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomBeneficiarioDetails implements UserDetails {

    private final Beneficiario beneficiario;

    public CustomBeneficiarioDetails(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + beneficiario.getRole().name();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return beneficiario.getSenha();
    }

    @Override
    public String getUsername() {
        return beneficiario.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}