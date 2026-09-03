package com.bsh.backend_sist_horto.gestao_mudas.specification;

import com.bsh.backend_sist_horto.gestao_mudas.dto.SolicitacaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class SolicitacaoSpecification {

    private SolicitacaoSpecification() {}

    private static Specification<Solicitacao> buscarPorDataMaiorOuIgual(LocalDate dataInicial) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("dataSolicitacao"),
                        dataInicial
                );
    }

    private static Specification<Solicitacao> buscarPorDataMenorOuIgual(LocalDate dataFinal) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("dataSolicitacao"),
                        dataFinal
                );
    }

    private static Specification<Solicitacao> buscarEntreDatas(LocalDate dataInicial, LocalDate dataFinal) {
        return (root, query, builder) ->
                builder.between(
                        root.get("dataSolicitacao"),
                        dataInicial,
                        dataFinal
                );
    }

    private static Specification<Solicitacao> statusSolicitacaoEquivalente(StatusSolicitacao statusSolicitacao) {
        return (root, query, builder) ->
                builder.equal(root.get("statusSolicitacao"), statusSolicitacao);
    }

    private static Specification<Solicitacao> buscarPorAno(Integer ano) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("parametroAnual").get("ano"),
                        ano
                );
    }

    private static Specification<Solicitacao> buscarPorNomeBeneficiario(String nome) {
        return (root, query, builder) ->
                builder.like(
                        builder.lower(
                                root.get("beneficiario").get("nome")
                        ),
                        "%" + nome.toLowerCase() + "%"
                );
    }

    private static Specification<Solicitacao> buscarPorCpfBeneficiario(String cpf) {
        return (root, query, builder) ->
                builder.like(
                        root.get("beneficiario").get("cpf"),
                        "%" + cpf + "%"
                );
    }

    public static Specification<Solicitacao> fromFilter(SolicitacaoFilter solicitacaoFilter) {

        if (solicitacaoFilter == null) {
            return (root, query, builder) -> builder.conjunction();
        }

        Specification<Solicitacao> solicitacaoSpecification = (root, query, builder) -> builder.conjunction();

        if (solicitacaoFilter.getDataInicial() != null && solicitacaoFilter.getDataFinal() != null) {
            solicitacaoSpecification = solicitacaoSpecification.and(
                    buscarEntreDatas(solicitacaoFilter.getDataInicial(), solicitacaoFilter.getDataFinal())
            );
        } else if (solicitacaoFilter.getDataInicial() != null) {
            solicitacaoSpecification = solicitacaoSpecification.and(
                    buscarPorDataMaiorOuIgual(solicitacaoFilter.getDataInicial())
            );
        } else if (solicitacaoFilter.getDataFinal() != null) {
            solicitacaoSpecification = solicitacaoSpecification.and(
                    buscarPorDataMenorOuIgual(solicitacaoFilter.getDataFinal())
            );
        }

        if(solicitacaoFilter.getStatusSolicitacao() != null){
            solicitacaoSpecification = solicitacaoSpecification
                    .and(statusSolicitacaoEquivalente(solicitacaoFilter.getStatusSolicitacao())
                    );
        }

        if(solicitacaoFilter.getAno() != null){
            solicitacaoSpecification = solicitacaoSpecification.and(
                    buscarPorAno(solicitacaoFilter.getAno())
            );
        }

        if(solicitacaoFilter.getNomeBeneficiario() != null && !solicitacaoFilter.getNomeBeneficiario().isBlank()){
            solicitacaoSpecification = solicitacaoSpecification.and(
                    buscarPorNomeBeneficiario(solicitacaoFilter.getNomeBeneficiario())
            );
        }

        if(solicitacaoFilter.getCpfBeneficiario() != null  && !solicitacaoFilter.getCpfBeneficiario().isBlank()){
            solicitacaoSpecification = solicitacaoSpecification.and(
                    buscarPorCpfBeneficiario(solicitacaoFilter.getCpfBeneficiario())
            );
        }

        return solicitacaoSpecification;
    }
}