package com.bsh.backend_sist_horto.gestao_mudas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlterarDisponibilidadeRequest {

    @NotNull(message = "Informe se a muda está disponível")
    private Boolean disponivel;

    @Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres")
    private String motivo;
}
