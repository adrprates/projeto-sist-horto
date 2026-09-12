package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PerfilResponse {
    private Long id;
    private String cpf;
    private String celular;
    private String telefone;
    private String email;
    private String nome;
    private String endereco;
    private String login;
}