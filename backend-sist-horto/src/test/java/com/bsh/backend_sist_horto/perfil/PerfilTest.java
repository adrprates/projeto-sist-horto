package com.bsh.backend_sist_horto.perfil;

import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.suporte.DadosTeste;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import com.bsh.backend_sist_horto.suporte.UsuarioTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Página de perfil (/perfil e /auth/atualizar-senha)")
class PerfilTest extends TesteIntegracaoBase {

    private static final String URL_SENHA = "/auth/atualizar-senha";

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static Map<String, Object> perfil(String nome, String email) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome);
        corpo.put("email", email);
        corpo.put("celular", "(34)91111-2222");
        corpo.put("telefone", "(34)3831-9999");
        corpo.put("endereco", "Av. Faria Pereira, 500");
        return corpo;
    }

    @Test
    @DisplayName("Retorna os dados do próprio usuário, sem expor a senha")
    void consultaPerfil() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        get("/perfil", morador)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value(morador.login()))
                .andExpect(jsonPath("$.cpf").value(morador.cpf()))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @DisplayName("Altera os dados de contato, sem permitir mudar CPF, login, perfil de acesso ou senha")
    void alteraDados() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();
        Map<String, Object> corpo = perfil("Nome Novo", "novo@email.com");
        corpo.put("cpf", "999.999.999-99");
        corpo.put("login", "outro.login");
        corpo.put("role", "ADMINISTRADOR");
        corpo.put("senha", "hackeada");

        putJson("/perfil", corpo, morador)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Novo"));

        Beneficiario salvo = beneficiarioRepository.findById(morador.id()).orElseThrow();
        assertThat(salvo.getEmail()).isEqualTo("novo@email.com");
        assertThat(salvo.getEndereco()).isEqualTo("Av. Faria Pereira, 500");
        assertThat(salvo.getCpf()).isEqualTo(morador.cpf());
        assertThat(salvo.getLogin()).isEqualTo(morador.login());
        assertThat(salvo.getRole()).isEqualTo(morador.beneficiario().getRole());
        assertThat(passwordEncoder.matches(DadosTeste.SENHA_PADRAO, salvo.getSenha())).isTrue();
    }

    @Test
    @DisplayName("Dados inválidos (e-mail ou nome em branco) são recusados e o cadastro não muda")
    void dadosInvalidos() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        putJson("/perfil", perfil("Nome", "email-invalido"), morador).andExpect(status().isBadRequest());
        putJson("/perfil", perfil("", "a@b.com"), morador).andExpect(status().isBadRequest());

        Beneficiario salvo = beneficiarioRepository.findById(morador.id()).orElseThrow();
        assertThat(salvo.getEmail()).isEqualTo(morador.beneficiario().getEmail());
        assertThat(salvo.getNome()).isEqualTo(morador.nome());
    }

    @Test
    @DisplayName("Troca de senha: a nova passa a valer, a antiga deixa de funcionar e o aviso de provisória some")
    void alteraSenha() throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();
        Beneficiario beneficiario = beneficiarioRepository.findById(morador.id()).orElseThrow();
        beneficiario.setSenhaProvisoria(true);
        beneficiarioRepository.save(beneficiario);

        putJson(URL_SENHA, DadosTeste.alterarSenha(morador.senha(), "novaSenha456", "novaSenha456"), morador)
                .andExpect(status().isOk());

        postJson("/auth/login", DadosTeste.login(morador.login(), "novaSenha456"), null).andExpect(status().isOk());
        postJson("/auth/login", DadosTeste.login(morador.login(), morador.senha()), null).andExpect(status().isBadRequest());
        get("/perfil", morador).andExpect(jsonPath("$.senhaProvisoria").value(false));
    }

    @ParameterizedTest(name = "{3}")
    @CsvSource(delimiter = '|', value = {
            "senhaErrada | novaSenha456 | novaSenha456 | Senha atual inválida",
            "senha123    | 123          | 123          | A nova senha deve ter pelo menos 6 caracteres",
            "senha123    | novaSenha456 | outraCoisa   | As senhas não coincidem"
    })
    @DisplayName("Troca de senha inválida é recusada e a senha atual continua valendo")
    void senhaInvalida(String atual, String nova, String confirmacao, String mensagem) throws Exception {
        UsuarioTeste morador = cenario.criarBeneficiario();

        putJson(URL_SENHA, DadosTeste.alterarSenha(atual, nova, confirmacao), morador)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(mensagem));

        postJson("/auth/login", DadosTeste.login(morador.login(), morador.senha()), null).andExpect(status().isOk());
    }
}
