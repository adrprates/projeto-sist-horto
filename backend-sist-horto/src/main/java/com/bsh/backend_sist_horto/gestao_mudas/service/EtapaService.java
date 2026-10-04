package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarEtapaRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.ItemPropostoRequest;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.EtapaSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemPropostaAlteracao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.EtapaSolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.MudaRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EtapaService {

    private static final String MENSAGEM_APROVADA =
            "Boa notícia: sua solicitação foi aprovada! Agora as mudas serão separadas no Horto Florestal. "
                    + "Avisaremos por aqui assim que estiverem prontas para retirada.";

    private static final String MENSAGEM_REJEITADA =
            "Infelizmente não foi possível atender à sua solicitação desta vez. "
                    + "Em caso de dúvidas, procure a Secretaria de Meio Ambiente.";

    private static final String MENSAGEM_PROPOSTA =
            "A Secretaria precisou ajustar os itens da sua solicitação. "
                    + "Confira as alterações abaixo e responda se aceita a nova proposta.";

    private static final String MENSAGEM_PRONTA =
            "Suas mudas estão separadas e esperando por você no Horto Florestal. "
                    + "Leve um documento com foto e faça a retirada até a data limite.";

    private static final String MENSAGEM_ENTREGUE =
            "Mudas entregues! Obrigado por ajudar a deixar Patrocínio mais verde.";

    private static final String MENSAGEM_EXPIRADA =
            "O prazo de retirada terminou e as mudas voltaram para o estoque. "
                    + "Se ainda tiver interesse, procure a Secretaria de Meio Ambiente.";

    private final EtapaSolicitacaoRepository etapaSolicitacaoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final MudaRepository mudaRepository;
    private final EstoqueService estoqueService;
    private final ValidadorLimitesService validadorLimites;

    public EtapaService(EtapaSolicitacaoRepository etapaSolicitacaoRepository,
                        SolicitacaoRepository solicitacaoRepository,
                        MudaRepository mudaRepository,
                        EstoqueService estoqueService,
                        ValidadorLimitesService validadorLimites) {
        this.etapaSolicitacaoRepository = etapaSolicitacaoRepository;
        this.solicitacaoRepository = solicitacaoRepository;
        this.mudaRepository = mudaRepository;
        this.estoqueService = estoqueService;
        this.validadorLimites = validadorLimites;
    }

    public EtapaSolicitacao salvar(
            Solicitacao solicitacao,
            StatusSolicitacao status,
            String descricao) {

        return salvar(solicitacao, status, null, descricao, null);
    }

    public EtapaSolicitacao salvar(
            Solicitacao solicitacao,
            StatusSolicitacao status,
            String motivo,
            String descricao,
            LocalDate dataLimiteRetirada) {

        EtapaSolicitacao etapa = new EtapaSolicitacao();

        etapa.setStatus(status);
        etapa.setMotivo(motivo);
        etapa.setDescricao(descricao);
        etapa.setDataHora(LocalDateTime.now());
        etapa.setDataLimiteRetirada(dataLimiteRetirada);

        solicitacao.adicionarEtapa(etapa);

        return etapaSolicitacaoRepository.save(etapa);
    }

    @Transactional
    public Solicitacao atualizarEtapa(Long idSolicitacao, AtualizarEtapaRequest request) {

        Solicitacao solicitacao = solicitacaoRepository
                .findById(idSolicitacao)
                .orElseThrow(() ->
                        new RuntimeException("Solicitação não encontrada.")
                );

        StatusSolicitacao novoStatus = request.getStatus();

        if (novoStatus == null) {
            throw new RuntimeException("Informe o novo status.");
        }

        validarTransicao(solicitacao.getStatusAtual(), novoStatus);

        String descricao = textoOuNulo(request.getDescricao());
        String motivo = textoOuNulo(request.getMotivo());

        switch (novoStatus) {

            case APROVADA -> {
                solicitacao.getItensPropostos().clear();
                registrar(solicitacao, StatusSolicitacao.APROVADA, null,
                        padrao(descricao, MENSAGEM_APROVADA), null);
            }

            case REJEITADA -> {
                if (motivo == null && descricao == null) {
                    throw new RuntimeException("Informe o motivo da rejeição.");
                }
                solicitacao.getItensPropostos().clear();
                registrar(solicitacao, StatusSolicitacao.REJEITADA, motivo,
                        padrao(descricao, MENSAGEM_REJEITADA), null);
            }

            case AGUARDANDO_CONFIRMACAO ->
                    proporAlteracao(solicitacao, descricao, request.getItensPropostos());

            case PRONTA_PARA_RETIRADA ->
                    marcarProntaRetirada(solicitacao, descricao, request.getDataLimiteRetirada());

            case ENTREGUE ->
                    marcarComoEntregue(solicitacao, descricao);

            case EXPIRADA ->
                    registrar(solicitacao, StatusSolicitacao.EXPIRADA, null,
                            padrao(descricao, MENSAGEM_EXPIRADA), null);

            default ->
                    throw new RuntimeException("Status inválido.");
        }

        return solicitacaoRepository.save(solicitacao);
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

        if (!aceitar) {
            solicitacao.getItensPropostos().clear();
            registrar(solicitacao, StatusSolicitacao.REJEITADA,
                    "Alteração recusada pelo beneficiário",
                    "O beneficiário recusou a alteração proposta e a solicitação foi encerrada.",
                    null);

            return solicitacaoRepository.save(solicitacao);
        }

        if (!solicitacao.getItensPropostos().isEmpty()) {
            aplicarProposta(solicitacao);
        }

        registrar(solicitacao, StatusSolicitacao.APROVADA, null,
                "O beneficiário aceitou a alteração proposta. Os novos itens passaram a valer e a solicitação segue aprovada.",
                null);

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

    private void proporAlteracao(
            Solicitacao solicitacao,
            String descricao,
            List<ItemPropostoRequest> itensInformados
    ) {

        if (itensInformados == null || itensInformados.isEmpty()) {
            throw new RuntimeException(
                    "Informe os itens da proposta. Para cancelar todos os itens, rejeite a solicitação."
            );
        }

        Map<Long, Integer> quantidadesPropostas = new LinkedHashMap<>();

        for (ItemPropostoRequest item : itensInformados) {
            if (item.getMudaId() == null || item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new RuntimeException("Cada item proposto precisa de uma muda e de uma quantidade maior que zero.");
            }
            quantidadesPropostas.merge(item.getMudaId(), item.getQuantidade(), Integer::sum);
        }

        Map<Long, Integer> quantidadesAtuais = quantidadesPorMuda(solicitacao.getItens());

        if (quantidadesPropostas.equals(quantidadesAtuais)) {
            throw new RuntimeException("A proposta é igual à solicitação atual. Altere ao menos um item.");
        }

        Map<Long, Muda> mudas = new LinkedHashMap<>();
        List<ItemSolicitacao> itensParaValidar = new ArrayList<>();

        for (Map.Entry<Long, Integer> proposto : quantidadesPropostas.entrySet()) {
            Muda muda = mudaRepository.findById(proposto.getKey())
                    .orElseThrow(() -> new RuntimeException("Muda não encontrada."));

            int aumento = proposto.getValue() - quantidadesAtuais.getOrDefault(muda.getId(), 0);

            if (aumento > 0 && !muda.isDisponivel()) {
                throw new RuntimeException(
                        "A muda " + nomeDa(muda) + " está indisponível e não pode ser incluída ou aumentada na proposta."
                );
            }

            if (aumento > 0 && !estoqueService.verificarDisponibilidadeComBloqueio(muda.getId(), aumento)) {
                throw new RuntimeException(
                        "Estoque insuficiente para " + nomeDa(muda) + ": restam "
                                + estoqueService.quantidadeDisponivel(muda.getId()) + " unidades disponíveis."
                );
            }

            ItemSolicitacao itemValidacao = new ItemSolicitacao();
            itemValidacao.setMuda(muda);
            itemValidacao.setQuantidade(proposto.getValue());
            itensParaValidar.add(itemValidacao);

            mudas.put(muda.getId(), muda);
        }

        validadorLimites.validar(solicitacao.getParametroAnual(), itensParaValidar);

        solicitacao.getItensPropostos().clear();

        for (Map.Entry<Long, Integer> proposto : quantidadesPropostas.entrySet()) {
            ItemPropostaAlteracao itemProposto = new ItemPropostaAlteracao();
            itemProposto.setMuda(mudas.get(proposto.getKey()));
            itemProposto.setQuantidade(proposto.getValue());
            solicitacao.adicionarItemProposto(itemProposto);
        }

        String resumo = resumirAlteracoes(solicitacao.getItens(), quantidadesPropostas, mudas);

        registrar(solicitacao, StatusSolicitacao.AGUARDANDO_CONFIRMACAO, null,
                padrao(descricao, MENSAGEM_PROPOSTA) + "\n\n" + resumo, null);
    }

    private void aplicarProposta(Solicitacao solicitacao) {
        Map<Long, Integer> quantidadesAtuais = quantidadesPorMuda(solicitacao.getItens());
        List<String> semEstoque = new ArrayList<>();

        for (ItemPropostaAlteracao proposto : solicitacao.getItensPropostos()) {
            int aumento = proposto.getQuantidade() - quantidadesAtuais.getOrDefault(proposto.getMuda().getId(), 0);

            if (aumento > 0 && !estoqueService.verificarDisponibilidadeComBloqueio(proposto.getMuda().getId(), aumento)) {
                semEstoque.add(nomeDa(proposto.getMuda()));
            }
        }

        if (!semEstoque.isEmpty()) {
            throw new RuntimeException(
                    "Algumas mudas da proposta não estão mais disponíveis (" + String.join(", ", semEstoque)
                            + "). Procure a Secretaria para uma nova proposta."
            );
        }

        solicitacao.getItens().clear();

        for (ItemPropostaAlteracao proposto : solicitacao.getItensPropostos()) {
            ItemSolicitacao novoItem = new ItemSolicitacao();
            novoItem.setMuda(proposto.getMuda());
            novoItem.setQuantidade(proposto.getQuantidade());
            solicitacao.adicionarItem(novoItem);
        }

        solicitacao.getItensPropostos().clear();
    }

    private void marcarProntaRetirada(
            Solicitacao solicitacao,
            String descricao,
            LocalDate dataLimiteRetirada
    ) {

        if (dataLimiteRetirada == null) {
            throw new RuntimeException("Informe a data limite de retirada.");
        }

        if (dataLimiteRetirada.isBefore(LocalDate.now())) {
            throw new RuntimeException("A data limite de retirada não pode estar no passado.");
        }

        registrar(solicitacao, StatusSolicitacao.PRONTA_PARA_RETIRADA, null,
                padrao(descricao, MENSAGEM_PRONTA), dataLimiteRetirada);
    }

    private void marcarComoEntregue(Solicitacao solicitacao, String descricao) {

        for (ItemSolicitacao item : solicitacao.getItens()) {
            estoqueService.removerQuantidade(
                    item.getMuda().getId(),
                    item.getQuantidade()
            );
        }

        registrar(solicitacao, StatusSolicitacao.ENTREGUE, null,
                padrao(descricao, MENSAGEM_ENTREGUE), null);
    }

    private void registrar(
            Solicitacao solicitacao,
            StatusSolicitacao status,
            String motivo,
            String descricao,
            LocalDate dataLimiteRetirada
    ) {
        solicitacao.setStatusAtual(status);
        salvar(solicitacao, status, motivo, descricao, dataLimiteRetirada);
    }

    private String resumirAlteracoes(
            List<ItemSolicitacao> itensAtuais,
            Map<Long, Integer> quantidadesPropostas,
            Map<Long, Muda> mudasPropostas
    ) {
        List<String> linhas = new ArrayList<>();

        for (ItemSolicitacao atual : itensAtuais) {
            Long mudaId = atual.getMuda().getId();
            Integer proposta = quantidadesPropostas.get(mudaId);

            if (proposta == null) {
                linhas.add("• " + nomeDa(atual.getMuda()) + ": removida da solicitação");
            } else if (!proposta.equals(atual.getQuantidade())) {
                linhas.add("• " + nomeDa(atual.getMuda()) + ": de " + atual.getQuantidade()
                        + " para " + proposta + " unidade(s)");
            }
        }

        Map<Long, Integer> quantidadesAtuais = quantidadesPorMuda(itensAtuais);

        for (Map.Entry<Long, Integer> proposto : quantidadesPropostas.entrySet()) {
            if (!quantidadesAtuais.containsKey(proposto.getKey())) {
                linhas.add("• " + nomeDa(mudasPropostas.get(proposto.getKey())) + ": incluída com "
                        + proposto.getValue() + " unidade(s)");
            }
        }

        return "Alterações propostas:\n" + String.join("\n", linhas);
    }

    private Map<Long, Integer> quantidadesPorMuda(List<ItemSolicitacao> itens) {
        Map<Long, Integer> quantidades = new LinkedHashMap<>();

        for (ItemSolicitacao item : itens) {
            quantidades.merge(item.getMuda().getId(), item.getQuantidade(), Integer::sum);
        }

        return quantidades;
    }

    private String nomeDa(Muda muda) {
        return muda.getNomesPopulares().get(0);
    }

    private String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    private String padrao(String texto, String mensagemPadrao) {
        return texto != null ? texto : mensagemPadrao;
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
                        && novo != StatusSolicitacao.REJEITADA
                        && novo != StatusSolicitacao.AGUARDANDO_CONFIRMACAO) {

                    throw new RuntimeException(
                            "Solicitações pendentes só podem ser aprovadas, rejeitadas ou receber uma proposta de alteração."
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
