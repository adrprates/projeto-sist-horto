package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.SaidaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.EtapaSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSaida;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Saida;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.ItemSaidaRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SaidaRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.SaidaSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SaidaService {

    private final SaidaRepository saidaRepository;
    private final ItemSaidaRepository itemSaidaRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final EstoqueService estoqueService;

    public SaidaService(SaidaRepository saidaRepository,
                        ItemSaidaRepository itemSaidaRepository,
                        SolicitacaoRepository solicitacaoRepository,
                        EstoqueService estoqueService) {
        this.saidaRepository = saidaRepository;
        this.itemSaidaRepository = itemSaidaRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.estoqueService = estoqueService;
    }

    public List<ItemSaida> listarItensPorFiltro(SaidaFilter saidaFilter) {
        return itemSaidaRepository.findAll(
                SaidaSpecification.fromFilter(saidaFilter),
                Sort.by("saida.dataSaida").ascending().and(Sort.by("id").ascending())
        );
    }

    @Transactional
    public Saida registrar(Solicitacao solicitacao) {
        if (saidaRepository.existsBySolicitacaoId(solicitacao.getId())) {
            throw new RuntimeException(
                    "Já existe uma saída registrada para esta solicitação"
            );
        }

        for (ItemSolicitacao item : solicitacao.getItens()) {
            estoqueService.removerQuantidade(
                    item.getMuda().getId(),
                    item.getQuantidade()
            );
        }

        return saidaRepository.save(montarSaida(solicitacao, LocalDate.now()));
    }

    @Transactional
    public int registrarSaidasAnteriores() {
        int registradas = 0;

        for (Solicitacao solicitacao : solicitacaoRepository.findByStatusAtual(StatusSolicitacao.ENTREGUE)) {
            if (saidaRepository.existsBySolicitacaoId(solicitacao.getId())) {
                continue;
            }

            LocalDate dataEntrega = solicitacao.getHistoricos().stream()
                    .filter(etapa -> etapa.getStatus() == StatusSolicitacao.ENTREGUE)
                    .map(EtapaSolicitacao::getDataHora)
                    .filter(dataHora -> dataHora != null)
                    .map(dataHora -> dataHora.toLocalDate())
                    .reduce((primeira, segunda) -> segunda)
                    .orElse(solicitacao.getDataSolicitacao());

            saidaRepository.save(montarSaida(solicitacao, dataEntrega));
            registradas++;
        }

        return registradas;
    }

    public Saida getSaidaPorId(Long id) {
        Optional<Saida> saidaOptional = saidaRepository.findById(id);
        Saida saida = null;
        if (saidaOptional.isPresent()) {
            saida = saidaOptional.get();
        } else {
            throw new RuntimeException("Saída não encontrada para o id: " + id);
        }
        return saida;
    }

    private Saida montarSaida(Solicitacao solicitacao, LocalDate dataSaida) {
        Saida saida = new Saida();
        saida.setSolicitacao(solicitacao);
        saida.setBeneficiario(solicitacao.getBeneficiario());
        saida.setParametroAnual(solicitacao.getParametroAnual());
        saida.setDataSaida(dataSaida);

        for (ItemSolicitacao itemSolicitado : solicitacao.getItens()) {
            ItemSaida itemSaida = new ItemSaida();
            itemSaida.setMuda(itemSolicitado.getMuda());
            itemSaida.setQuantidade(itemSolicitado.getQuantidade());
            saida.adicionarItem(itemSaida);
        }

        return saida;
    }
}
