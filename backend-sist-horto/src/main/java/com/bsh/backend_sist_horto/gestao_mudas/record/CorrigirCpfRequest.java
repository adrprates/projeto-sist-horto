package com.bsh.backend_sist_horto.gestao_mudas.record;

import jakarta.validation.constraints.NotBlank;

public record CorrigirCpfRequest(@NotBlank(message = "CPF é obrigatório")
                                 String cpf) {
}
