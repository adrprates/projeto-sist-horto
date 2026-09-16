package com.bsh.backend_sist_horto.gestao_mudas.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarSenhaRequest(@NotBlank(message = "Senha é obrigatória")
                                    String senhaAtual,

                                    @NotBlank(message = "Nova senha é obrigatória")
                                    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
                                    String novaSenha,

                                    @NotBlank
                                    String confirmarNovaSenha) {
}