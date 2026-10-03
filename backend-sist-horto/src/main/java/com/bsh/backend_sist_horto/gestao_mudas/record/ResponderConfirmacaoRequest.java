package com.bsh.backend_sist_horto.gestao_mudas.record;

import jakarta.validation.constraints.NotNull;

public record ResponderConfirmacaoRequest(@NotNull(message = "Informe se aceita ou recusa a alteração")
                                          Boolean aceitar) {
}
