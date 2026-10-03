package com.bsh.backend_sist_horto.gestao_mudas.record;

public record CredenciaisResponse(Long beneficiarioId,
                                  String nome,
                                  String login,
                                  String senhaProvisoria) {
}
