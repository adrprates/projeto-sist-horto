package com.bsh.backend_sist_horto.beneficiario;

import com.bsh.backend_sist_horto.suporte.DadosTeste;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Gestão de beneficiários pelo administrador (/beneficiarios)")
class BeneficiarioAdminTest extends TesteIntegracaoBase {

    private UsuarioTeste admin;

    @BeforeEach
    void preparar() {
        admin = cenario.criarAdministrador();
    }

    @Test
    @DisplayName("Cadastro assistido gera login sem dados pessoais e senha provisória que funcionam no login")
    void cadastroAssistido() throws Exception {
        String resposta = postJson("/beneficiarios", DadosTeste.cadastroAssistidoValido(DadosTeste.cpfUnico()), admin)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login", matchesPattern("horto\\d{5}")))
                .andExpect(jsonPath("$.senhaProvisoria", matchesPattern("[A-Za-z2-9]{8}")))
                .andReturn().getResponse().getContentAsString();

        postJson("/auth/login", DadosTeste.login(JsonPath.read(resposta, "$.login"),
                JsonPath.read(resposta, "$.senhaProvisoria")), null)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cadastro assistido com CPF já existente retorna 409")
    void cpfDuplicado() throws Exception {
        UsuarioTeste existente = cenario.criarBeneficiario();

        postJson("/beneficiarios", DadosTeste.cadastroAssistidoValido(DadosTeste.somenteDigitos(existente.cpf())), admin)
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Listagem filtra por nome, pagina e nunca expõe a senha")
    void listagem() throws Exception {
        cenario.criarBeneficiario("jose1", "José Pereira");
        cenario.criarBeneficiario("jose2", "Maria José");
        cenario.criarBeneficiario("ana", "Ana Lima");

        get("/beneficiarios?nome=JOSÉ&tamanho=1", admin)
                .andExpect(jsonPath("$.totalElementos").value(2))
                .andExpect(jsonPath("$.conteudo", hasSize(1)))
                .andExpect(jsonPath("$.conteudo[*].senha").isEmpty());
    }

    @Test
    @DisplayName("Correção de CPF: formata o novo valor e recusa CPF de outra pessoa")
    void corrigeCpf() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();
        UsuarioTeste outro = cenario.criarBeneficiario();

        patchJson("/beneficiarios/" + morador.id() + "/cpf", Map.of("cpf", outro.cpf()), admin)
                .andExpect(status().isConflict());
        patchJson("/beneficiarios/" + morador.id() + "/cpf", Map.of("cpf", "98765432100"), admin)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("987.654.321-00"));
    }

    @Test
    @DisplayName("Redefinir senha invalida a anterior; não vale para administradores")
    void redefineSenha() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        String resposta = postSemCorpo("/beneficiarios/" + morador.id() + "/redefinir-senha", admin)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        postJson("/auth/login", DadosTeste.login(morador.login(), morador.senha()), null)
                .andExpect(status().isBadRequest());
        postJson("/auth/login", DadosTeste.login(morador.login(), JsonPath.read(resposta, "$.senhaProvisoria")), null)
                .andExpect(status().isOk());

        postSemCorpo("/beneficiarios/" + cenario.criarAdministrador().id() + "/redefinir-senha", admin)
                .andExpect(status().isBadRequest());
    }
}
