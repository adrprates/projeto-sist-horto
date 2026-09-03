package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.SolicitacaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.SolicitacaoSpecification;
import org.springframework.stereotype.Service;

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

    public Solicitacao salvar(Solicitacao solicitacao) {
        solicitacao.setStatusSolicitacao(StatusSolicitacao.PENDENTE);
        return solicitacaoRepository.save(solicitacao);
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

    public void detelar(Solicitacao solicitacao) {
        solicitacaoRepository.delete(solicitacao);
    }
}