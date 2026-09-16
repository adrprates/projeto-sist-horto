package com.bsh.backend_sist_horto.gestao_mudas.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank(message = "CPF é obrigatório")
                              String cpf,

                              @NotBlank(message = "Celular é obrigatório")
                              String celular,

                              String telefone,

                              @NotBlank(message = "E-mail é obrigatório")
                              @Email(message = "E-mail inválido")
                              String email,

                              @NotBlank(message = "Nome é obrigatório")
                              String nome,

                              @NotBlank(message = "Endereço é obrigatório")
                              String endereco,

                              @NotBlank(message = "Login é obrigatório")
                              String login,

                              @NotBlank(message = "Senha é obrigatória")
                              @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
                              String senha,

                              @NotBlank
                              String confirmarSenha){
}