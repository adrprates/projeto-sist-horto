package com.bsh.backend_sist_horto.gestao_mudas.record;

public record AtualizarCredenciaisRequest(String senhaAtual,
                                          String novoLogin,
                                          String novaSenha) {
}