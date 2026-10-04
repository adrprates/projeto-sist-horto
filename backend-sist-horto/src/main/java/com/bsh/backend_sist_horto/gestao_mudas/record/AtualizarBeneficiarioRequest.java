package com.bsh.backend_sist_horto.gestao_mudas.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarBeneficiarioRequest(@NotBlank(message = "Nome é obrigatório")
                                           String nome,

                                           @NotBlank(message = "E-mail é obrigatório")
                                           @Email(message = "E-mail inválido")
                                           String email,

                                           @NotBlank(message = "Celular é obrigatório")
                                           String celular,

                                           String telefone,

                                           @NotBlank(message = "Endereço é obrigatório")
                                           String endereco) {
}
