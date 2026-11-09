package com.bsh.backend_sist_horto.suporte;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Base dos testes de integração: sobe a aplicação completa (segurança, filtros JWT,
 * controllers, serviços e JPA) sobre um H2 em memória e faz as chamadas HTTP pelo MockMvc.
 *
 * Os métodos auxiliares recebem o {@link UsuarioTeste} que faz a chamada; passe
 * {@code null} para simular um visitante sem login.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class TesteIntegracaoBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JsonMapper jsonMapper;

    @Autowired
    protected CenarioTeste cenario;

    @Autowired
    private LimpezaBancoDados limpezaBancoDados;

    @BeforeEach
    void limparBanco() {
        limpezaBancoDados.limpar();
    }

    // ------------------------------------------------------------------
    // Requisições
    // ------------------------------------------------------------------

    protected ResultActions get(String url, UsuarioTeste usuario, Object... variaveis) throws Exception {
        return mockMvc.perform(autenticar(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(url, variaveis), usuario));
    }

    protected ResultActions postJson(String url, Object corpo, UsuarioTeste usuario) throws Exception {
        return mockMvc.perform(comCorpo(post(url), corpo, usuario));
    }

    protected ResultActions putJson(String url, Object corpo, UsuarioTeste usuario) throws Exception {
        return mockMvc.perform(comCorpo(put(url), corpo, usuario));
    }

    protected ResultActions patchJson(String url, Object corpo, UsuarioTeste usuario) throws Exception {
        return mockMvc.perform(comCorpo(patch(url), corpo, usuario));
    }

    protected ResultActions postSemCorpo(String url, UsuarioTeste usuario) throws Exception {
        return mockMvc.perform(autenticar(post(url), usuario));
    }

    protected ResultActions patchSemCorpo(String url, UsuarioTeste usuario) throws Exception {
        return mockMvc.perform(autenticar(patch(url), usuario));
    }

    protected ResultActions deletar(String url, UsuarioTeste usuario) throws Exception {
        return mockMvc.perform(autenticar(delete(url), usuario));
    }

    /** Envia uma requisição com um token arbitrário (inválido, adulterado, expirado...). */
    protected ResultActions getComToken(String url, String token) throws Exception {
        return mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + token));
    }

    protected String json(Object objeto) {
        return jsonMapper.writeValueAsString(objeto);
    }

    private MockHttpServletRequestBuilder comCorpo(MockHttpServletRequestBuilder builder,
                                                   Object corpo,
                                                   UsuarioTeste usuario) {
        return autenticar(builder.contentType(MediaType.APPLICATION_JSON).content(json(corpo)), usuario);
    }

    private MockHttpServletRequestBuilder autenticar(MockHttpServletRequestBuilder builder, UsuarioTeste usuario) {
        if (usuario != null) {
            builder.header("Authorization", "Bearer " + usuario.token());
        }
        return builder;
    }
}
