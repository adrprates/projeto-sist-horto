package com.bsh.backend_sist_horto.gestao_mudas.dto;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BeneficiarioFilter {

    private String cpf;
    private String nome;
    private String email;
    private Role role;
}