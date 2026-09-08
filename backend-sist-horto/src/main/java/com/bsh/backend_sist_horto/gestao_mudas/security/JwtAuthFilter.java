package com.bsh.backend_sist_horto.gestao_mudas.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class JwtAuthFilter extends GenericFilter {

    private final JwtUtil jwtUtil;
    private final CustomBeneficiarioDetailsService customBeneficiarioDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil,  CustomBeneficiarioDetailsService customBeneficiarioDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customBeneficiarioDetailsService = customBeneficiarioDetailsService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var decodedJWT = jwtUtil.parse(token);
                String username = decodedJWT.getSubject();

                UserDetails user = customBeneficiarioDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // Se o token for inválido, expirado ou houver erro,
                // a autenticação simplesmente não é setada
                // e o Spring Security bloqueará se a rota exigir.
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}