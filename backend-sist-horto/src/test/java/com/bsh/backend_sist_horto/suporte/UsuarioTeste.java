package com.bsh.backend_sist_horto.suporte;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;

/**
 * Usuário criado para um teste, já com o token JWT pronto para ser enviado
 * no cabeçalho Authorization.
 *
 * @param beneficiario entidade persistida
 * @param senha        senha em texto puro (a do banco está criptografada)
 * @param token        token JWT válido para o usuário
 */
public record UsuarioTeste(Beneficiario beneficiario, String senha, String token) {

    public Long id() {
        return beneficiario.getId();
    }

    public String login() {
        return beneficiario.getLogin();
    }

    public String nome() {
        return beneficiario.getNome();
    }

    public String cpf() {
        return beneficiario.getCpf();
    }

    public boolean isAdministrador() {
        return beneficiario.getRole() == Role.ADMINISTRADOR;
    }
}
