package com.bsh.backend_sist_horto.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bsh.backend_sist_horto.gestao_mudas.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Testes unitários (sem Spring) da geração e validação dos tokens JWT. */
@DisplayName("JWT: geração, assinatura e validação do token")
class JwtUtilTest {

    private static final String SEGREDO = "segredo-de-teste-unitario";
    private static final long VALIDADE_MS = 3_600_000;

    private final JwtUtil jwtUtil = criarJwtUtil(SEGREDO, VALIDADE_MS);
    private final UserDetails beneficiario = User.withUsername("maria")
            .password("ignorada")
            .authorities(new SimpleGrantedAuthority("ROLE_BENEFICIARIO"))
            .build();

    private static JwtUtil criarJwtUtil(String segredo, long validadeMs) {
        return new JwtUtil(new MockEnvironment()
                .withProperty("app.jwt.secret", segredo)
                .withProperty("app.jwt.expiration", String.valueOf(validadeMs)));
    }

    @Test
    @DisplayName("O token usa HMAC256, guarda login e perfil, não contém a senha e tem a validade configurada")
    void conteudoDoToken() {
        String token = jwtUtil.generateToken(beneficiario);
        DecodedJWT decodificado = jwtUtil.parse(token);

        assertThat(decodificado.getAlgorithm()).isEqualTo("HS256");
        assertThat(decodificado.getSubject()).isEqualTo("maria");
        assertThat(decodificado.getClaim("role").asString()).isEqualTo("ROLE_BENEFICIARIO");
        assertThat(decodificado.getExpiresAt().getTime() - decodificado.getIssuedAt().getTime()).isEqualTo(VALIDADE_MS);
        assertThat(new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8))
                .doesNotContain("ignorada");
    }

    @Test
    @DisplayName("Token expirado é rejeitado")
    void tokenExpirado() {
        String token = criarJwtUtil(SEGREDO, -1_000).generateToken(beneficiario);

        assertThatThrownBy(() -> jwtUtil.parse(token)).isInstanceOf(TokenExpiredException.class);
    }

    @Test
    @DisplayName("Token assinado com outro segredo é rejeitado")
    void outroSegredo() {
        String tokenFalso = criarJwtUtil("segredo-de-um-atacante", VALIDADE_MS).generateToken(beneficiario);

        assertThatThrownBy(() -> jwtUtil.parse(tokenFalso)).isInstanceOf(SignatureVerificationException.class);
    }

    @Test
    @DisplayName("Trocar o perfil para administrador dentro do token invalida a assinatura")
    void conteudoAdulterado() {
        String[] partes = jwtUtil.generateToken(beneficiario).split("\\.");
        String conteudo = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8)
                .replace("ROLE_BENEFICIARIO", "ROLE_ADMINISTRADOR");
        String adulterado = partes[0] + "."
                + Base64.getUrlEncoder().withoutPadding().encodeToString(conteudo.getBytes(StandardCharsets.UTF_8))
                + "." + partes[2];

        assertThatThrownBy(() -> jwtUtil.parse(adulterado)).isInstanceOf(SignatureVerificationException.class);
    }

    @Test
    @DisplayName("Token sem assinatura (alg: none) é rejeitado")
    void tokenSemAssinatura() {
        String semAssinatura = JWT.create()
                .withSubject("maria")
                .withExpiresAt(new Date(System.currentTimeMillis() + VALIDADE_MS))
                .sign(Algorithm.none());

        assertThatThrownBy(() -> jwtUtil.parse(semAssinatura)).isInstanceOf(JWTVerificationException.class);
    }
}
