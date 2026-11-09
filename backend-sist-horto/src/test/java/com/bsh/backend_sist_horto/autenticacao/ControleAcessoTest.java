package com.bsh.backend_sist_horto.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante que cada perfil só alcança as rotas permitidas em SecurityConfig e que
 * tokens adulterados, forjados ou expirados não dão acesso.
 */
@DisplayName("Segurança: controle de acesso por perfil e validação do token")
class ControleAcessoTest extends TesteIntegracaoBase {

    private static final String SEGREDO_DOS_TESTES = "segredo-exclusivo-dos-testes-automatizados";

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    private void chamar(String metodo, String url, UsuarioTeste usuario, int statusEsperado) throws Exception {
        var builder = request(HttpMethod.valueOf(metodo), url).contentType("application/json").content("{}");
        if (usuario != null) {
            builder.header("Authorization", "Bearer " + usuario.token());
        }
        mockMvc.perform(builder).andExpect(status().is(statusEsperado));
    }

    private String token(String login, String role, long expiraEmMs, String segredo) {
        return JWT.create()
                .withSubject(login)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiraEmMs))
                .sign(Algorithm.HMAC256(segredo));
    }

    @Test
    @DisplayName("Visitante consulta o catálogo, mas não acessa rotas que exigem login")
    void visitante() throws Exception {
        get("/mudas", null).andExpect(status().isOk());
        chamar("GET", "/perfil", null, 403);
        chamar("POST", "/solicitacoes/rascunho/adicionar", null, 403);
    }

    @ParameterizedTest(name = "{0} {1} → 403")
    @CsvSource({
            "POST, /mudas",
            "POST, /estoques/1/adicionar?quantidade=10",
            "POST, /parametros",
            "GET, /beneficiarios",
            "GET, /solicitacoes",
            "POST, /solicitacoes/1/etapas",
            "GET, /relatorios/estoque"
    })
    @DisplayName("Beneficiário não acessa rotas administrativas")
    void beneficiarioBloqueado(String metodo, String url) throws Exception {
        chamar(metodo, url, cenario.criarBeneficiario(), 403);
    }

    @Test
    @DisplayName("Administrador acessa as rotas administrativas e também as de beneficiário")
    void administrador() throws Exception {
        UsuarioTeste admin = cenario.criarAdministrador();

        get("/beneficiarios", admin).andExpect(status().isOk());
        get("/relatorios/estoque", admin).andExpect(status().isOk());
        get("/solicitacoes/minhas-solicitacoes", admin).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Token malformado, forjado com outro segredo ou expirado não autentica")
    void tokensInvalidos() throws Exception {
        UsuarioTeste admin = cenario.criarAdministrador();

        getComToken("/perfil", "isto-nao-e-um-jwt").andExpect(status().isForbidden());
        getComToken("/beneficiarios", token(admin.login(), "ROLE_ADMINISTRADOR", 60_000, "segredo-adivinhado"))
                .andExpect(status().isForbidden());
        getComToken("/perfil", token(admin.login(), "ROLE_ADMINISTRADOR", -60_000, SEGREDO_DOS_TESTES))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("O perfil vem do banco: token válido com a claim 'role' trocada não dá acesso de administrador")
    void perfilVemDoBanco() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        getComToken("/beneficiarios", token(morador.login(), "ROLE_ADMINISTRADOR", 60_000, SEGREDO_DOS_TESTES))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Token de usuário excluído deixa de funcionar")
    void usuarioExcluido() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();
        beneficiarioRepository.deleteById(morador.id());

        get("/perfil", morador).andExpect(status().isForbidden());
    }
}
