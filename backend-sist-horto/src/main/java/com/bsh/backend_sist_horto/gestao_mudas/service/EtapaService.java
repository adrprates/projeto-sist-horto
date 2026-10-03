package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.EtapaSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.EtapaSolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EtapaService {

    private final EtapaSolicitacaoRepository etapaSolicitacaoRepository;

    private final SolicitacaoRepository solicitacaoRepository;
    private final EstoqueService estoqueService;

    public EtapaService(EtapaSolicitacaoRepository etapaSolicitacaoRepository,
                        SolicitacaoRepository solicitacaoRepository,
                        EstoqueService estoqueService) {
        this.etapaSolicitacaoRepository = etapaSolicitacaoRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.estoqueService = estoqueService;
    }

    public EtapaSolicitacao salvar(
            Solicitacao solicitacao,
            StatusSolicitacao status,
            String descricao) {

        return salvar(
                solicitacao,
                status,
                descricao,
                null
        );
    }

    public EtapaSolicitacao salvar(
            Solicitacao solicitacao,
            StatusSolicitacao status,
            String descricao,
            LocalDate dataLimiteRetirada) {

        EtapaSolicitacao etapa = new EtapaSolicitacao();

        etapa.setSolicitacao(solicitacao);
        etapa.setStatus(status);
        etapa.setDescricao(descricao);
        etapa.setDataHora(LocalDateTime.now());
        etapa.setDataLimiteRetirada(dataLimiteRetirada);

        return etapaSolicitacaoRepository.save(etapa);
    }

    @Transactional
    public Solicitacao atualizarEtapa(
            Long idSolicitacao,
            StatusSolicitacao novoStatus,
            String descricao,
            LocalDate dataLimiteRetirada
    ) {

        Solicitacao solicitacao = solicitacaoRepository
                .findById(idSolicitacao)
                .orElseThrow(() ->
                        new RuntimeException("Solicitação não encontrada.")
                );

        validarTransicao(
                solicitacao.getStatusAtual(),
                novoStatus
        );

        switch (novoStatus) {

            case APROVADA ->
                    aprovarSolicitacao(
                            solicitacao,
                            descricao
                    );

            case REJEITADA ->
                    rejeitarSolicitacao(
                            solicitacao,
                            descricao
                    );

            case PRONTA_PARA_RETIRADA ->
                    marcarProntaRetirada(
                            solicitacao,
                            descricao,
                            dataLimiteRetirada
                    );

            case ENTREGUE ->
                    marcarComoEntregue(
                            solicitacao,
                            descricao
                    );

            case EXPIRADA ->
                    expirarSolicitacao(
                            solicitacao
                    );

            case AGUARDANDO_CONFIRMACAO ->
                    aguardarConfirmacao(
                            solicitacao,
                            descricao
                    );

            default ->
                    throw new RuntimeException(
                            "Status inválido."
                    );
        }

        return solicitacao;
    }

    @Transactional
    public Solicitacao responderConfirmacao(
            Long idSolicitacao,
            boolean aceitar,
            Beneficiario beneficiario
    ) {

        Solicitacao solicitacao = solicitacaoRepository
                .findById(idSolicitacao)
                .orElseThrow(() ->
                        new RuntimeException("Solicitação não encontrada.")
                );

        if (!solicitacao.getBeneficiario().getId().equals(beneficiario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta solicitação não pertence a você."
            );
        }

        if (solicitacao.getStatusAtual() != StatusSolicitacao.AGUARDANDO_CONFIRMACAO) {
            throw new RuntimeException(
                    "Esta solicitação não está aguardando a sua confirmação."
            );
        }

        if (aceitar) {
            aprovarSolicitacao(
                    solicitacao,
                    "O beneficiário aceitou a alteração proposta. A solicitação segue aprovada."
            );
        } else {
            rejeitarSolicitacao(
                    solicitacao,
                    "O beneficiário recusou a alteração proposta. A solicitação foi encerrada."
            );
        }

        return solicitacaoRepository.save(solicitacao);
    }

    public EtapaSolicitacao getEtapaSolicitacaoById(Long id) {
        Optional<EtapaSolicitacao> etapaSolicitacaoOptional = etapaSolicitacaoRepository.findById(id);
        if (etapaSolicitacaoOptional.isPresent()) {
            return etapaSolicitacaoOptional.get();
        } else {
            throw new RuntimeException("Etada da solicitação não encontrada com o id: " + id);
        }
    }

    private void aprovarSolicitacao(
            Solicitacao solicitacao,
            String descricao
    ) {

        solicitacao.setStatusAtual(
                StatusSolicitacao.APROVADA
        );

        salvar(
                solicitacao,
                StatusSolicitacao.APROVADA,
                descricao != null && !descricao.isBlank()
                        ? descricao
                        : "Sua solicitação foi aprovada."
        );
    }

    private void rejeitarSolicitacao(
            Solicitacao solicitacao,
            String descricao
    ) {

        if(descricao == null || descricao.isBlank()){

            throw new RuntimeException(
                    "Informe o motivo da rejeição."
            );
        }

        solicitacao.setStatusAtual(
                StatusSolicitacao.REJEITADA
        );

        salvar(
                solicitacao,
                StatusSolicitacao.REJEITADA,
                descricao
        );
    }

    private void marcarProntaRetirada(
            Solicitacao solicitacao,
            String descricao,
            LocalDate dataLimiteRetirada
    ) {

        if(dataLimiteRetirada == null){

            throw new RuntimeException(
                    "Informe a data limite de retirada."
            );
        }

        solicitacao.setStatusAtual(
                StatusSolicitacao.PRONTA_PARA_RETIRADA
        );

        salvar(
                solicitacao,
                StatusSolicitacao.PRONTA_PARA_RETIRADA,
                descricao != null && !descricao.isBlank()
                        ? descricao
                        : "Suas mudas estão prontas para retirada.",
                dataLimiteRetirada
        );
    }

    private void marcarComoEntregue(
            Solicitacao solicitacao,
            String descricao
    ) {

        for(ItemSolicitacao item :
                solicitacao.getItens()) {

            estoqueService.removerQuantidade(
                    item.getMuda().getId(),
                    item.getQuantidade()
            );
        }

        solicitacao.setStatusAtual(
                StatusSolicitacao.ENTREGUE
        );

        salvar(
                solicitacao,
                StatusSolicitacao.ENTREGUE,
                descricao != null && !descricao.isBlank()
                        ? descricao
                        : "Entrega realizada."
        );
    }

    private void expirarSolicitacao(
            Solicitacao solicitacao
    ) {

        solicitacao.setStatusAtual(
                StatusSolicitacao.EXPIRADA
        );

        salvar(
                solicitacao,
                StatusSolicitacao.EXPIRADA,
                "Prazo de retirada expirado."
        );
    }

    private void aguardarConfirmacao(
            Solicitacao solicitacao,
            String descricao
    ) {

        if(descricao == null || descricao.isBlank()){

            throw new RuntimeException(
                    "Informe a alteração proposta."
            );
        }

        solicitacao.setStatusAtual(
                StatusSolicitacao.AGUARDANDO_CONFIRMACAO
        );

        salvar(
                solicitacao,
                StatusSolicitacao.AGUARDANDO_CONFIRMACAO,
                descricao
        );
    }

    private void validarTransicao(
            StatusSolicitacao atual,
            StatusSolicitacao novo) {

        switch (atual) {

            case RASCUNHO -> {
                if (novo != StatusSolicitacao.PENDENTE) {
                    throw new RuntimeException(
                            "Rascunho só pode ser enviado para análise."
                    );
                }
            }

            case PENDENTE -> {
                if (novo != StatusSolicitacao.APROVADA
                        && novo != StatusSolicitacao.REJEITADA) {

                    throw new RuntimeException(
                            "Solicitações pendentes só podem ser aprovadas ou rejeitadas."
                    );
                }
            }

            case APROVADA -> {
                if (novo != StatusSolicitacao.PRONTA_PARA_RETIRADA
                        && novo != StatusSolicitacao.AGUARDANDO_CONFIRMACAO) {

                    throw new RuntimeException(
                            "Solicitações aprovadas só podem seguir para retirada ou confirmação."
                    );
                }
            }

            case AGUARDANDO_CONFIRMACAO -> {
                if (novo != StatusSolicitacao.APROVADA
                        && novo != StatusSolicitacao.REJEITADA) {

                    throw new RuntimeException(
                            "Aguardando confirmação só pode voltar para aprovada ou rejeitada."
                    );
                }
            }

            case PRONTA_PARA_RETIRADA -> {
                if (novo != StatusSolicitacao.ENTREGUE
                        && novo != StatusSolicitacao.EXPIRADA) {

                    throw new RuntimeException(
                            "Solicitações prontas para retirada só podem ser entregues ou expiradas."
                    );
                }
            }

            case ENTREGUE,
                 REJEITADA,
                 EXPIRADA ->

                    throw new RuntimeException(
                            "Esta solicitação já foi finalizada."
                    );
        }
    }
}