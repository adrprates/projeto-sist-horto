package com.bsh.backend_sist_horto.autenticacao;

import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.suporte.DadosTeste;
import com.bsh.backend_sist_horto.suporte.TesteIntegracaoBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Registro de beneficiário (POST /auth/registro)")
class RegistroTest extends TesteIntegracaoBase {

    private static final String URL = "/auth/registro";

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Registra com os valores enviados, CPF formatado, senha em BCrypt e perfil BENEFICIARIO")
    void registra() throws Exception {
        Map<String, Object> corpo = DadosTeste.registroValido("ana.souza", "11122233344");
        corpo.put("role", "ADMINISTRADOR"); //tentativa de escalar privilégio deve ser ignorada

        postJson(URL, corpo, null).andExpect(status().isCreated());

        Beneficiario salvo = beneficiarioRepository.findByLogin("ana.souza").orElseThrow();
        assertThat(salvo.getCpf()).isEqualTo("111.222.333-44");
        assertThat(salvo.getNome()).isEqualTo(corpo.get("nome"));
        assertThat(salvo.getEmail()).isEqualTo(corpo.get("email"));
        assertThat(salvo.getRole()).isEqualTo(Role.BENEFICIARIO);
        assertThat(salvo.getSenha()).startsWith("$2").isNotEqualTo(DadosTeste.SENHA_PADRAO);
        assertThat(passwordEncoder.matches(DadosTeste.SENHA_PADRAO, salvo.getSenha())).isTrue();
    }

    @Test
    @DisplayName("CPF já cadastrado (mesmo com outra máscara) retorna 409")
    void cpfDuplicado() throws Exception {
        postJson(URL, DadosTeste.registroValido("primeiro", "555.666.777-88"), null).andExpect(status().isCreated());

        postJson(URL, DadosTeste.registroValido("segundo", "55566677788"), null)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("CPF já cadastrado"));

        assertThat(beneficiarioRepository.findByLogin("segundo")).isEmpty();
    }

    @Test
    @DisplayName("Login já cadastrado retorna 409")
    void loginDuplicado() throws Exception {
        postJson(URL, DadosTeste.registroValido("mesmo.login", DadosTeste.cpfUnico()), null).andExpect(status().isCreated());

        postJson(URL, DadosTeste.registroValido("mesmo.login", DadosTeste.cpfUnico()), null)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Login já cadastrado"));

        assertThat(beneficiarioRepository.count()).isEqualTo(1);
    }

    @ParameterizedTest(name = "{0} = \"{1}\"")
    @CsvSource(delimiter = '|', value = {
            "nome           | ' '",
            "email          | sem-arroba",
            "cpf            | 123.456.789-0",
            "senha          | 12345",
            "confirmarSenha | outraSenha"
    })
    @DisplayName("Dados inválidos são recusados com 400 e nada é gravado")
    void dadosInvalidos(String campo, String valor) throws Exception {
        Map<String, Object> corpo = DadosTeste.registroValido("usuario", DadosTeste.cpfUnico());
        corpo.put(campo, valor);
        if (campo.equals("senha")) {
            corpo.put("confirmarSenha", valor);
        }

        postJson(URL, corpo, null).andExpect(status().isBadRequest());

        assertThat(beneficiarioRepository.count()).isZero();
    }
}
