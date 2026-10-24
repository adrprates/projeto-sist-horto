package com.bsh.backend_sist_horto.gestao_mudas.specification;

import com.bsh.backend_sist_horto.gestao_mudas.dto.DistribuicaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Distribuicao;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class DistribuicaoSpecification {

    private DistribuicaoSpecification() {}

    private static Specification<Distribuicao> buscarPorDataMaiorOuIgual(LocalDate dataInicial){
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("dataRetirada"),
                        dataInicial
                );
    }

    private static Specification<Distribuicao> buscarPorDataMenorOuIgual(LocalDate dataFinal){
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("dataRetirada"),
                        dataFinal
                );
    }

    private static Specification<Distribuicao> buscarEntreDatas(LocalDate dataInicial, LocalDate dataFinal){
        return (root, query, builder) ->
                builder.between(
                        root.get("dataRetirada"),
                        dataInicial,
                        dataFinal
                );
    }

    private static Specification<Distribuicao> buscarPorAno(Integer ano) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("parametroAnual").get("ano"),
                        ano
                );
    }

    private static Specification<Distribuicao> buscarPorNomeBeneficiario(String nome) {
        return (root, query, builder) ->
                builder.like(
                        builder.lower(
                                root.get("beneficiario").get("nome")
                        ),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    private static Specification<Distribuicao> buscarPorCpfBeneficiario(String cpf) {
        return (root, query, builder) ->
                builder.like(
                        root.get("beneficiario").get("cpf"),
                        "%" + cpf + "%"
                );
    }

    public static Specification<Distribuicao> fromFilter(DistribuicaoFilter distribuicaoFilter) {
        if (distribuicaoFilter == null) {
            return (root, query, builder) -> builder.conjunction();
        }

        Specification<Distribuicao> distribuicaoSpecification = (root, query, builder) -> builder.conjunction();

        if (distribuicaoFilter.getDataInicial() != null && distribuicaoFilter.getDataFinal() != null) {
            distribuicaoSpecification = distribuicaoSpecification.and(
                    buscarEntreDatas(distribuicaoFilter.getDataInicial(), distribuicaoFilter.getDataFinal())
            );
        } else if (distribuicaoFilter.getDataInicial() != null) {
            distribuicaoSpecification = distribuicaoSpecification.and(
                    buscarPorDataMaiorOuIgual(distribuicaoFilter.getDataInicial())
            );
        } else if (distribuicaoFilter.getDataFinal() != null) {
            distribuicaoSpecification = distribuicaoSpecification.and(
                    buscarPorDataMenorOuIgual(distribuicaoFilter.getDataFinal())
            );
        }

        if(distribuicaoFilter.getAno() != null){
            distribuicaoSpecification = distribuicaoSpecification.and(
                    buscarPorAno(distribuicaoFilter.getAno())
            );
        }

        if(distribuicaoFilter.getNomeBeneficiario() != null && !distribuicaoFilter.getNomeBeneficiario().isBlank()){
            distribuicaoSpecification = distribuicaoSpecification.and(
                    buscarPorNomeBeneficiario(distribuicaoFilter.getNomeBeneficiario())
            );
        }

        if(distribuicaoFilter.getCpfBeneficiario() != null  && !distribuicaoFilter.getCpfBeneficiario().isBlank()){
            distribuicaoSpecification = distribuicaoSpecification.and(
                    buscarPorCpfBeneficiario(distribuicaoFilter.getCpfBeneficiario())
            );
        }

        return distribuicaoSpecification;
    }
}