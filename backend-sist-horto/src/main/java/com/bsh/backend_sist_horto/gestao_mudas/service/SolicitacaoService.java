package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.SolicitacaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.SolicitacaoSpecification;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public List<Solicitacao> listarPorFiltro(SolicitacaoFilter solicitacaoFilter) {
        return solicitacaoRepository.findAll(SolicitacaoSpecification.fromFilter(solicitacaoFilter));
    }

    @Transactional
    public Solicitacao salvar(Solicitacao solicitacao, List<ItemSolicitacao> itensSolicitacao) {
        solicitacao.setDataSolicitacao(LocalDate.now());
        solicitacao.setStatusSolicitacao(StatusSolicitacao.PENDENTE);

        if(itensSolicitacao != null) {
            for (ItemSolicitacao itemSolicitacao : itensSolicitacao) {
                solicitacao.adicionarItem(itemSolicitacao);
            }
        }

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public void atualizarStatus(Long idSolicitacao, StatusSolicitacao statusSolicitacao) {
        Solicitacao solicitacao = getSolicitacaoPorId(idSolicitacao);
        solicitacao.setStatusSolicitacao(statusSolicitacao);
    }

    public Solicitacao getSolicitacaoPorId(Long id) {
        Optional<Solicitacao> solicitacaoOptional = solicitacaoRepository.findById(id);
        Solicitacao solicitacao = null;
        if (solicitacaoOptional.isPresent()) {
            solicitacao = solicitacaoOptional.get();
        } else {
            throw new RuntimeException("Solicitação não encontrada para o id: " + id);
        }
        return solicitacao;
    }

    public void deletar(Solicitacao solicitacao) {
        solicitacaoRepository.delete(solicitacao);
    }
}