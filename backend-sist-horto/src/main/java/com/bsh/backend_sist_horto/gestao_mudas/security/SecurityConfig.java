package com.bsh.backend_sist_horto.gestao_mudas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ======================
                        // VISITANTE
                        // ======================
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/mudas/**"
                        ).permitAll()

                        // ======================
                        // BENEFICIARIO OU ADMIN
                        // ======================
                        .requestMatchers(
                                "/solicitacoes/**"
                        ).hasAnyRole("BENEFICIARIO", "ADMIN")

                        .requestMatchers(
                                "/beneficiario/me/**"
                        ).hasAnyRole("BENEFICIARIO", "ADMIN")

                        .requestMatchers(
                                "/auth/atualizar/**"
                        ).hasAnyRole("BENEFICIARIO", "ADMIN")

                        // ======================
                        // SOMENTE ADMIN
                        // ======================
                        .requestMatchers(
                                HttpMethod.POST,
                                "/mudas"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/mudas/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/mudas/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/estoques/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/estoques/**"
                        ).hasRole("ADMINISTRADOR")

                        .requestMatchers(
                                "/beneficiario/**"
                        ).hasRole("ADMINISTRADOR")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}