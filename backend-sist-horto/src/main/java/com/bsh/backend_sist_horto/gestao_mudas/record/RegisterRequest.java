package com.bsh.backend_sist_horto.gestao_mudas.record;

public record RegisterRequest(String cpf,
                              String celular,
                              String telefone,
                              String email,
                              String nome,
                              String endereco,
                              String login,
                              String senha) {
}