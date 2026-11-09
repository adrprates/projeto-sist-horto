package com.bsh.backend_sist_horto.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bsh.backend_sist_horto.suporte.DadosTeste;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Login (POST /auth/login)")
class LoginTest extends TesteIntegracaoBase {

    private static final String URL = "/auth/login";

    @Test
    @DisplayName("Credenciais corretas devolvem um token do usuário que dá acesso às rotas autenticadas")
    void loginComSucesso() throws Exception {
        UsuarioTeste admin = cenario.criarAdministrador();

        String resposta = postJson(URL, DadosTeste.login(admin.login(), admin.senha()), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senhaProvisoria").value(false))
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(resposta, "$.token");
        DecodedJWT decodificado = JWT.decode(token);
        assertThat(decodificado.getSubject()).isEqualTo(admin.login());
        assertThat(decodificado.getClaim("role").asString()).isEqualTo("ROLE_ADMINISTRADOR");

        getComToken("/perfil", token).andExpect(jsonPath("$.login").value(admin.login()));
    }

    @Test
    @DisplayName("Senha incorreta é recusada sem emitir token")
    void senhaIncorreta() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        postJson(URL, DadosTeste.login(morador.login(), morador.senha().toUpperCase()), null)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @DisplayName("Usuário inexistente recebe a mesma resposta de senha incorreta (não revela quais logins existem)")
    void naoRevelaUsuariosExistentes() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        String senhaErrada = postJson(URL, DadosTeste.login(morador.login(), "senhaErrada"), null)
                .andReturn().getResponse().getContentAsString();
        String inexistente = postJson(URL, DadosTeste.login("ninguem", "senhaErrada"), null)
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertThat(inexistente).isEqualTo(senhaErrada);
    }

    @Test
    @DisplayName("Usuário de cadastro assistido é avisado de que a senha é provisória")
    void avisaSenhaProvisoria() throws Exception {
        String credenciais = postJson("/beneficiarios", DadosTeste.cadastroAssistidoValido(DadosTeste.cpfUnico()),
                cenario.criarAdministrador()).andReturn().getResponse().getContentAsString();

        postJson(URL, DadosTeste.login(JsonPath.read(credenciais, "$.login"),
                JsonPath.read(credenciais, "$.senhaProvisoria")), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senhaProvisoria").value(true));
    }
}
