package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.DadosMudaResumo;
import com.bsh.backend_sist_horto.gestao_mudas.dto.ItemRelatorioEstoque;
import com.bsh.backend_sist_horto.gestao_mudas.dto.ItemRelatorioSaida;
import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioEstoqueFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioEstoqueResponse;
import com.bsh.backend_sist_horto.gestao_mudas.dto.RelatorioSaidaResponse;
import com.bsh.backend_sist_horto.gestao_mudas.dto.ResumoEstoqueCategoria;
import com.bsh.backend_sist_horto.gestao_mudas.dto.ResumoSaidaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.dto.SaidaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.SituacaoEstoque;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSaida;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RelatorioService {

    private static final int LIMITE_ESTOQUE_BAIXO = 10;

    private final SaidaService saidaService;
    private final MudaService mudaService;

    public RelatorioService(SaidaService saidaService, MudaService mudaService) {
        this.saidaService = saidaService;
        this.mudaService = mudaService;
    }

    @Transactional
    public RelatorioSaidaResponse gerarRelatorioSaidas(SaidaFilter saidaFilter) {
        List<ItemSaida> itensSaida = saidaService.listarItensPorFiltro(saidaFilter);

        List<ItemRelatorioSaida> itens = new ArrayList<>();
        Map<Long, ResumoSaidaMuda> resumoPorMuda = new LinkedHashMap<>();
        Map<Long, Set<Long>> beneficiariosPorMuda = new LinkedHashMap<>();
        Set<Long> saidas = new HashSet<>();
        Set<Long> beneficiarios = new HashSet<>();
        int totalMudas = 0;

        for (ItemSaida itemSaida : itensSaida) {
            Muda muda = itemSaida.getMuda();
            Long beneficiarioId = itemSaida.getSaida().getBeneficiario().getId();
            String nomeMuda = primeiroNome(muda.getNomesPopulares());

            itens.add(new ItemRelatorioSaida(
                    itemSaida.getSaida().getDataSaida(),
                    itemSaida.getSaida().getSolicitacao().getId(),
                    nomeMuda,
                    muda.getCategoria(),
                    itemSaida.getQuantidade(),
                    itemSaida.getSaida().getBeneficiario().getNome()
            ));

            ResumoSaidaMuda resumo = resumoPorMuda.computeIfAbsent(muda.getId(), id ->
                    new ResumoSaidaMuda(id, nomeMuda, muda.getCategoria(), 0, 0));
            resumo.setQuantidade(resumo.getQuantidade() + itemSaida.getQuantidade());

            Set<Long> beneficiariosDaMuda = beneficiariosPorMuda.computeIfAbsent(muda.getId(), id -> new HashSet<>());
            beneficiariosDaMuda.add(beneficiarioId);
            resumo.setTotalBeneficiarios(beneficiariosDaMuda.size());

            saidas.add(itemSaida.getSaida().getId());
            beneficiarios.add(beneficiarioId);
            totalMudas += itemSaida.getQuantidade();
        }

        List<ResumoSaidaMuda> resumo = new ArrayList<>(resumoPorMuda.values());
        resumo.sort(Comparator.comparing(ResumoSaidaMuda::getQuantidade).reversed()
                .thenComparing(ResumoSaidaMuda::getNomeMuda));

        return new RelatorioSaidaResponse(
                totalMudas,
                saidas.size(),
                beneficiarios.size(),
                resumo,
                itens
        );
    }

    @Transactional
    public RelatorioEstoqueResponse gerarRelatorioEstoque(RelatorioEstoqueFilter relatorioEstoqueFilter) {
        MudaFilter mudaFilter = new MudaFilter();
        SituacaoEstoque situacaoFiltrada = null;

        if (relatorioEstoqueFilter != null) {
            mudaFilter.setCategoria(relatorioEstoqueFilter.getCategoria());
            situacaoFiltrada = relatorioEstoqueFilter.getSituacao();
        }

        List<ItemRelatorioEstoque> itens = new ArrayList<>();
        Map<CategoriaMuda, ResumoEstoqueCategoria> resumoPorCategoria = new EnumMap<>(CategoriaMuda.class);
        int estoqueFisico = 0;
        int quantidadeReservada = 0;
        int estoqueDisponivel = 0;

        for (DadosMudaResumo muda : mudaService.listarResumo(mudaFilter)) {
            SituacaoEstoque situacao = definirSituacao(muda);

            if (situacaoFiltrada != null && situacao != situacaoFiltrada) {
                continue;
            }

            itens.add(new ItemRelatorioEstoque(
                    muda.getId(),
                    primeiroNome(muda.getNomesPopulares()),
                    muda.getCategoria(),
                    muda.getEstoqueTotal(),
                    muda.getQuantidadeReservada(),
                    muda.getEstoqueDisponivel(),
                    situacao,
                    muda.getMotivoIndisponibilidade()
            ));

            if (muda.getCategoria() != null) {
                ResumoEstoqueCategoria resumo = resumoPorCategoria.computeIfAbsent(muda.getCategoria(), categoria ->
                        new ResumoEstoqueCategoria(categoria, 0, 0, 0, 0));
                resumo.setTotalEspecies(resumo.getTotalEspecies() + 1);
                resumo.setEstoqueFisico(resumo.getEstoqueFisico() + muda.getEstoqueTotal());
                resumo.setQuantidadeReservada(resumo.getQuantidadeReservada() + muda.getQuantidadeReservada());
                resumo.setEstoqueDisponivel(resumo.getEstoqueDisponivel() + muda.getEstoqueDisponivel());
            }

            estoqueFisico += muda.getEstoqueTotal();
            quantidadeReservada += muda.getQuantidadeReservada();
            estoqueDisponivel += muda.getEstoqueDisponivel();
        }

        itens.sort(Comparator.comparing(ItemRelatorioEstoque::getNomeMuda));

        return new RelatorioEstoqueResponse(
                itens.size(),
                estoqueFisico,
                quantidadeReservada,
                estoqueDisponivel,
                new ArrayList<>(resumoPorCategoria.values()),
                itens
        );
    }

    private SituacaoEstoque definirSituacao(DadosMudaResumo muda) {
        if (!muda.isDisponivel()) {
            return SituacaoEstoque.INDISPONIVEL;
        }
        if (muda.getEstoqueDisponivel() <= 0) {
            return SituacaoEstoque.ESGOTADA;
        }
        if (muda.getEstoqueDisponivel() <= LIMITE_ESTOQUE_BAIXO) {
            return SituacaoEstoque.ESTOQUE_BAIXO;
        }
        return SituacaoEstoque.DISPONIVEL;
    }

    private String primeiroNome(List<String> nomesPopulares) {
        if (nomesPopulares == null || nomesPopulares.isEmpty()) {
            return "Sem nome";
        }
        return nomesPopulares.get(0);
    }
}
