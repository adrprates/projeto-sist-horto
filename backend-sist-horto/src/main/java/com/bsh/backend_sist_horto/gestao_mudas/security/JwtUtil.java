package com.bsh.backend_sist_horto.gestao_mudas.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final Algorithm algorithm;
    private final long expirationMs;

    public JwtUtil(Environment env) {
        String secret = env.getProperty("app.jwt.secret", "chave-secreta-padrao-segura");
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMs = Long.parseLong(env.getProperty("app.jwt.expiration", "3600000"));
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public DecodedJWT parse(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}