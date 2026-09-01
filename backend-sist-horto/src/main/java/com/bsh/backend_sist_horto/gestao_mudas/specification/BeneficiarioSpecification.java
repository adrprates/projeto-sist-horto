package com.bsh.backend_sist_horto.gestao_mudas.specification;

import com.bsh.backend_sist_horto.gestao_mudas.dto.BeneficiarioFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.Role;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import org.springframework.data.jpa.domain.Specification;

public final class BeneficiarioSpecification {

    private BeneficiarioSpecification(){}

    private static Specification<Beneficiario> buscarPorCpf(String cpf) {
        return (root, query, builder) ->
                builder.like(
                        root.get("cpf"),
                        "%" + cpf + "%"
                );
    }

    private static Specification<Beneficiario> buscarPorNome(String nome) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("nome")),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    private static Specification<Beneficiario> buscarPorEmail(String email) {
        return (root, query, builder) ->
                builder.like(builder.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                );
    }

    private static Specification<Beneficiario> buscarPorRole(Role role) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("role"), role);
    }

    public static Specification<Beneficiario> fromFilter(BeneficiarioFilter beneficiarioFilter) {

        if (beneficiarioFilter == null) {
            return (root, query, builder) -> builder.conjunction();
        }

        Specification<Beneficiario> beneficiarioSpecification =
                (root, query, builder) ->
                        builder.conjunction();

        if(beneficiarioFilter.getCpf() != null && !beneficiarioFilter.getCpf().isBlank()) {
            beneficiarioSpecification = beneficiarioSpecification.and(buscarPorCpf(beneficiarioFilter.getCpf()));
        }

        if (beneficiarioFilter.getNome() != null  && !beneficiarioFilter.getNome().isBlank()) {
            beneficiarioSpecification = beneficiarioSpecification.and(buscarPorNome(beneficiarioFilter.getNome()));
        }

        if(beneficiarioFilter.getEmail() != null  && !beneficiarioFilter.getEmail().isBlank()) {
            beneficiarioSpecification = beneficiarioSpecification.and(buscarPorEmail(beneficiarioFilter.getEmail()));
        }

        if (beneficiarioFilter.getRole() != null) {
            beneficiarioSpecification = beneficiarioSpecification.and(buscarPorRole(beneficiarioFilter.getRole()));
        }

        return beneficiarioSpecification;
    }
}