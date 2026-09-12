package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class AtualizarPerfilRequest {
    private String nome;
    private String email;
    private String celular;
    private String telefone;
    private String endereco;
}