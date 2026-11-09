package com.bsh.backend_sist_horto.documentacao;

import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Documentação da API (Swagger / OpenAPI)")
class DocumentacaoSwaggerTest extends TesteIntegracaoBase {

    @Test
    @DisplayName("A especificação é pública, descreve as rotas e configura o token JWT")
    void especificacao() throws Exception {
        get("/v3/api-docs", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Sistema Horto — API de Doação de Mudas"))
                .andExpect(jsonPath("$.paths['/solicitacoes/{id}/etapas']").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/auth/login'].post.security", empty()))
                .andExpect(jsonPath("$.paths['/perfil'].get.parameters").doesNotExist());
    }

    @Test
    @DisplayName("A interface do Swagger UI é acessível sem login")
    void swaggerUi() throws Exception {
        get("/swagger-ui/index.html", null).andExpect(status().isOk());
    }
}
