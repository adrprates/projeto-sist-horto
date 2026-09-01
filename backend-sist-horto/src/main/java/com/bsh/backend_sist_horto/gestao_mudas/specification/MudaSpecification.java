package com.bsh.backend_sist_horto.gestao_mudas.specification;

import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class MudaSpecification {

    private MudaSpecification() {}

    private static Specification<Muda> buscarPorNomePopular(String nomePopular) {
        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            Join<Muda, String> nomesPopulares =
                    root.join("nomesPopulares");

            return criteriaBuilder.like(
                    criteriaBuilder.lower(nomesPopulares),
                    "%" + nomePopular.toLowerCase() + "%"
            );

        };
    }

    private static Specification<Muda> categoriaEquivalente(CategoriaMuda categoria) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("categoria"), categoria);
    }

    private static Specification<Muda> perdeMuitasFolhas(Boolean perdeMuitasFolhas){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("perdeMuitasFolhas"), perdeMuitasFolhas);
    }

    private static Specification<Muda> comFlores(Boolean possuiFlores) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("possuiFlores"), possuiFlores);
    }

    private static Specification<Muda> comFrutos(Boolean possuiFrutos) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("possuiFrutos"), possuiFrutos);
    }

    public static Specification<Muda> fromFilter(MudaFilter mudaFilter) {

        if (mudaFilter == null) {
            return (root, query, builder) -> builder.conjunction();
        }

        Specification<Muda> mudaSpecification = (root, query, builder) -> builder.conjunction();

        if(mudaFilter.getNomePopular() != null && !mudaFilter.getNomePopular().isBlank()) {
            mudaSpecification = mudaSpecification.and(buscarPorNomePopular(mudaFilter.getNomePopular()));
        }

        if(mudaFilter.getCategoria() != null){
            mudaSpecification = mudaSpecification.and(categoriaEquivalente(mudaFilter.getCategoria()));
        }

        if(mudaFilter.getPerdeMuitasFolhas() != null){
            mudaSpecification = mudaSpecification.and(perdeMuitasFolhas(mudaFilter.getPerdeMuitasFolhas()));
        }

        if(mudaFilter.getPossuiFlores() != null){
            mudaSpecification = mudaSpecification.and(comFlores(mudaFilter.getPossuiFlores()));
        }

        if(mudaFilter.getPossuiFrutos() != null){
            mudaSpecification = mudaSpecification.and(comFrutos(mudaFilter.getPossuiFrutos()));
        }

        return mudaSpecification;
    }
}