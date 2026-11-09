package com.bsh.backend_sist_horto.gestao_mudas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração da documentação OpenAPI exibida no Swagger UI.
 *
 * <ul>
 *     <li>Swagger UI: <a href="http://localhost:8080/swagger-ui.html">/swagger-ui.html</a></li>
 *     <li>Especificação JSON: <a href="http://localhost:8080/v3/api-docs">/v3/api-docs</a></li>
 * </ul>
 *
 * Para testar rotas protegidas: faça o login em <b>POST /auth/login</b>, copie o token
 * retornado e informe-o no botão <b>Authorize</b> (sem o prefixo "Bearer ").
 */
@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_SEGURANCA = "bearerAuth";

    @Bean
    OpenAPI sistemaHortoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Horto — API de Doação de Mudas")
                        .version("1.0.0")
                        .description("""
                                API do sistema de doação de mudas do Horto Florestal de Patrocínio/MG, \
                                mantido pela Secretaria Municipal de Agricultura e Pecuária.

                                **Níveis de acesso**
                                - **Visitante** (sem token): login, registro e consulta ao catálogo de mudas.
                                - **Beneficiário**: monta e acompanha a própria solicitação, perfil e senha.
                                - **Administrador**: gestão de mudas, estoque, beneficiários, parâmetros anuais, \
                                solicitações e relatórios.

                                **Autenticação**: obtenha o token em `POST /auth/login` e informe-o no botão \
                                *Authorize*. O token JWT (HMAC256) expira em 1 hora por padrão.

                                **Erros**: as respostas de erro seguem o formato `{"message": "descrição"}`.""")
                        .contact(new Contact()
                                .name("Secretaria Municipal de Agricultura e Pecuária de Patrocínio/MG")))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_SEGURANCA, new SecurityScheme()
                                .name(ESQUEMA_SEGURANCA)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT retornado por POST /auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURANCA))
                .tags(List.of(
                        new Tag().name("Autenticação").description("Login, registro e troca de senha"),
                        new Tag().name("Perfil").description("Dados do usuário autenticado"),
                        new Tag().name("Mudas").description("Catálogo e gestão das espécies"),
                        new Tag().name("Estoque").description("Controle de quantidade das mudas (administrador)"),
                        new Tag().name("Parâmetros Anuais").description("Limites de doação de cada ano"),
                        new Tag().name("Solicitações").description("Rascunho, envio e acompanhamento das solicitações"),
                        new Tag().name("Solicitações (Administração)").description("Análise e atendimento das solicitações pela Secretaria"),
                        new Tag().name("Beneficiários").description("Cadastro e manutenção dos beneficiários (administrador)"),
                        new Tag().name("Relatórios").description("Saída de mudas e posição de estoque (administrador)"),
                        new Tag().name("GBIF").description("Consulta de dados taxonômicos na API pública do GBIF")
                ));
    }
}
