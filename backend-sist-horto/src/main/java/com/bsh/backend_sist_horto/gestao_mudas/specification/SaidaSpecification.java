package com.bsh.backend_sist_horto.gestao_mudas.specification;

import com.bsh.backend_sist_horto.gestao_mudas.dto.SaidaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSaida;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class SaidaSpecification {

    private SaidaSpecification() {}

    private static Specification<ItemSaida> buscarPorDataMaiorOuIgual(LocalDate dataInicial){
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("saida").get("dataSaida"),
                        dataInicial
                );
    }

    private static Specification<ItemSaida> buscarPorDataMenorOuIgual(LocalDate dataFinal){
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("saida").get("dataSaida"),
                        dataFinal
                );
    }

    private static Specification<ItemSaida> buscarEntreDatas(LocalDate dataInicial, LocalDate dataFinal){
        return (root, query, builder) ->
                builder.between(
                        root.get("saida").get("dataSaida"),
                        dataInicial,
                        dataFinal
                );
    }

    private static Specification<ItemSaida> buscarPorCategoria(CategoriaMuda categoria) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("muda").get("categoria"),
                        categoria
                );
    }

    private static Specification<ItemSaida> buscarPorMuda(Long mudaId) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("muda").get("id"),
                        mudaId
                );
    }

    public static Specification<ItemSaida> fromFilter(SaidaFilter saidaFilter) {
        if (saidaFilter == null) {
            return (root, query, builder) -> builder.conjunction();
        }

        Specification<ItemSaida> saidaSpecification = (root, query, builder) -> builder.conjunction();

        if (saidaFilter.getDataInicial() != null && saidaFilter.getDataFinal() != null) {
            saidaSpecification = saidaSpecification.and(
                    buscarEntreDatas(saidaFilter.getDataInicial(), saidaFilter.getDataFinal())
            );
        } else if (saidaFilter.getDataInicial() != null) {
            saidaSpecification = saidaSpecification.and(
                    buscarPorDataMaiorOuIgual(saidaFilter.getDataInicial())
            );
        } else if (saidaFilter.getDataFinal() != null) {
            saidaSpecification = saidaSpecification.and(
                    buscarPorDataMenorOuIgual(saidaFilter.getDataFinal())
            );
        }

        if (saidaFilter.getCategoria() != null) {
            saidaSpecification = saidaSpecification.and(
                    buscarPorCategoria(saidaFilter.getCategoria())
            );
        }

        if (saidaFilter.getMudaId() != null) {
            saidaSpecification = saidaSpecification.and(
                    buscarPorMuda(saidaFilter.getMudaId())
            );
        }

        return saidaSpecification;
    }
}
