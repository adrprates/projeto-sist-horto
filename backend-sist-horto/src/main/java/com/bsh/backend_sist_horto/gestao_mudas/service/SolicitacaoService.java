package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.ParametroAnualDisponivel;
import com.bsh.backend_sist_horto.gestao_mudas.dto.SolicitacaoBeneficiarioResumo;
import com.bsh.backend_sist_horto.gestao_mudas.dto.SolicitacaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.*;
import com.bsh.backend_sist_horto.gestao_mudas.repository.MudaRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.ParametroAnualRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.SolicitacaoSpecification;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ParametroAnualRepository parametroAnualRepository;
    private final MudaRepository mudaRepository;

    private final EstoqueService estoqueService;
    private final EtapaService etapaService;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,
                              ParametroAnualRepository parametroAnualRepository,
                              EstoqueService estoqueService,
                              MudaRepository mudaRepository,
                              EtapaService etapaService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.parametroAnualRepository = parametroAnualRepository;
        this.estoqueService = estoqueService;
        this.mudaRepository = mudaRepository;
        this.etapaService = etapaService;
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public List<Solicitacao> listarPorFiltro(SolicitacaoFilter solicitacaoFilter) {
        return solicitacaoRepository.findAll(SolicitacaoSpecification.fromFilter(solicitacaoFilter));
    }

    public Solicitacao montarSolicitacao(Long mudaId, Integer quantidade, Beneficiario beneficiario) {
        Solicitacao solicitacaoRascunho = buscarOuCriarRascunho(beneficiario);

        Muda muda = mudaRepository.findById(mudaId).orElseThrow(() ->
                new RuntimeException("Muda não encontrada"));

        ItemSolicitacao itemExistente = null;

        for(ItemSolicitacao item : solicitacaoRascunho.getItens()){
            if (item.getMuda().getId().equals(mudaId)) {
                itemExistente = item;
                break;
            }
        }

        if (itemExistente != null) {

            itemExistente.setQuantidade(
                    itemExistente.getQuantidade() + quantidade
            );

        } else {

            ItemSolicitacao novoItem = new ItemSolicitacao();

            novoItem.setMuda(muda);
            novoItem.setQuantidade(quantidade);

            solicitacaoRascunho.adicionarItem(novoItem);
        }

        validarLimites(
                solicitacaoRascunho.getParametroAnual(),
                solicitacaoRascunho.getItens()
        );

        boolean possuiSaldo =
                estoqueService.verificarDisponibilidade(
                        mudaId,
                        itemExistente != null
                                ? itemExistente.getQuantidade()
                                : quantidade
                );

        if (!possuiSaldo) {
            throw new RuntimeException(
                    "Estoque insuficiente para a muda selecionada."
            );
        }

        return solicitacaoRepository.save(
                solicitacaoRascunho
        );
    }

    public Solicitacao buscarOuCriarRascunho(Beneficiario beneficiario) {
        Optional <Solicitacao> solicitacaoRascunho = solicitacaoRepository.findByBeneficiarioAndStatusAtual(
                beneficiario, StatusSolicitacao.RASCUNHO
        );

        if(solicitacaoRascunho.isPresent()){
            return solicitacaoRascunho.get();
        }

        Integer anoAtual = LocalDate.now().getYear();

        ParametroAnual parametroAnual =
                parametroAnualRepository.findById(anoAtual)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Parâmetro anual não cadastrado para o ano "
                                                + anoAtual
                                )
                        );

        Solicitacao novaSolicitacao = new Solicitacao();

        novaSolicitacao.setBeneficiario(beneficiario);
        novaSolicitacao.setParametroAnual(parametroAnual);
        novaSolicitacao.setStatusAtual(StatusSolicitacao.RASCUNHO);
        novaSolicitacao.setDataSolicitacao(LocalDate.now());

        return solicitacaoRepository.save(novaSolicitacao);
    }

    @Transactional
    public Solicitacao enviarSolicitacao(Long id) {
        return enviarSolicitacao(id, "Sua solicitação foi enviada para análise.");
    }

    @Transactional
    public Solicitacao enviarRascunhoDoBeneficiario(Beneficiario beneficiario, String nomeResponsavel) {
        Solicitacao rascunho = buscarOuCriarRascunho(beneficiario);

        return enviarSolicitacao(
                rascunho.getId(),
                "Solicitação registrada pela Secretaria por " + nomeResponsavel + " e enviada para análise."
        );
    }

    public Solicitacao getSolicitacaoDoBeneficiario(Long id, Beneficiario beneficiario) {
        Solicitacao solicitacao = getSolicitacaoPorId(id);

        if (!solicitacao.getBeneficiario().getId().equals(beneficiario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta solicitação não pertence a você."
            );
        }

        return solicitacao;
    }

    @Transactional
    public Solicitacao enviarSolicitacao(Long id, String descricaoEtapa) {
       Solicitacao solicitacao = getSolicitacaoPorId(id);

       if(solicitacao.getStatusAtual() != StatusSolicitacao.RASCUNHO){
           throw new RuntimeException("A solicitação já foi enviada.");
       }

        if (solicitacao.getItens().isEmpty()) {
            throw new RuntimeException(
                    "A solicitação não possui itens."
            );
        }

        List<String> mudasEmFalta = new ArrayList<>();

        for (ItemSolicitacao item : solicitacao.getItens()) {

            Long mudaId = item.getMuda().getId();
            Integer quantidade = item.getQuantidade();

            boolean possuiSaldo =
                    estoqueService.verificarDisponibilidade(
                            mudaId,
                            quantidade
                    );

            if (!possuiSaldo) {

                String nomeMuda =
                        item.getMuda()
                                .getNomesPopulares()
                                .get(0);

                mudasEmFalta.add(
                        nomeMuda + " (" +
                                quantidade + " solicitadas)"
                );
            }
        }

        if (!mudasEmFalta.isEmpty()) {
            throw new RuntimeException(
                    "Estoque insuficiente para: " +
                            String.join(", ", mudasEmFalta)
            );
        }

        validarLimites(
                solicitacao.getParametroAnual(),
                solicitacao.getItens()
        );

        solicitacao.setStatusAtual(
                StatusSolicitacao.PENDENTE
        );

        etapaService.salvar(solicitacao, StatusSolicitacao.PENDENTE, descricaoEtapa);

        return solicitacaoRepository.save(
                solicitacao
        );
    }

    public void validarLimites(ParametroAnual parametroAnual, List<ItemSolicitacao> itensSolicitacao) {

        if (parametroAnual == null) {
            throw new RuntimeException("Parâmetro anual não definido para esta solicitação.");
        }

        int totalFrutiferas = 0;
        int totalOutras = 0;

        Map<Long, Integer> qtdPorMudaFrutifera = new HashMap<>();
        Map<Long, Integer> qtdPorMudaOutras = new HashMap<>();

        for (ItemSolicitacao item : itensSolicitacao) {
            Muda muda = item.getMuda();
            int quantidade = item.getQuantidade();

            boolean isFrutifera = muda.getCategoria() == CategoriaMuda.FRUTIFERAS;

            if (isFrutifera) {
                totalFrutiferas += quantidade;

                int qtdAtual = qtdPorMudaFrutifera.getOrDefault(muda.getId(), 0) + quantidade;
                if (qtdAtual > parametroAnual.getMaxPorEspecieFrutifera()) {
                    throw new RuntimeException(
                            "A espécie '" + muda.getNomesPopulares().get(0) + "' ultrapassa o limite máximo de " +
                                    parametroAnual.getMaxPorEspecieFrutifera() + " unidades por espécie frutífera."
                    );
                }
                qtdPorMudaFrutifera.put(muda.getId(), qtdAtual);

            } else {
                totalOutras += quantidade;

                int qtdAtual = qtdPorMudaOutras.getOrDefault(muda.getId(), 0) + quantidade;
                if (qtdAtual > parametroAnual.getMaxPorEspecieOutras()) {
                    throw new RuntimeException(
                            "A espécie '" + muda.getNomesPopulares().get(0) + "' ultrapassa o limite máximo de " +
                                    parametroAnual.getMaxPorEspecieOutras() + " unidades por espécie."
                    );
                }
                qtdPorMudaOutras.put(muda.getId(), qtdAtual);
            }
        }

        if (totalFrutiferas > parametroAnual.getLimiteFrutiferas()) {
            throw new RuntimeException(
                    "O total de mudas frutíferas solicitadas (" + totalFrutiferas +
                            ") excede o limite anual permitido (" + parametroAnual.getLimiteFrutiferas() + ")."
            );
        }

        if (totalOutras > parametroAnual.getLimiteOutras()) {
            throw new RuntimeException(
                    "O total de outras mudas solicitadas (" + totalOutras +
                            ") excede o limite anual permitido (" + parametroAnual.getLimiteOutras() + ")."
            );
        }

        int totalGeral = totalFrutiferas + totalOutras;
        if (totalGeral > parametroAnual.getLimiteTotalMudas()) {
            throw new RuntimeException(
                    "O total geral de mudas solicitadas (" + totalGeral +
                            ") excede o limite global do programa (" + parametroAnual.getLimiteTotalMudas() + ")."
            );
        }
    }

    @Transactional
    public Solicitacao atualizarQuantidadeItem(
            Long itemId,
            Integer quantidade,
            Beneficiario beneficiario) {

        Solicitacao solicitacao =
                buscarOuCriarRascunho(beneficiario);

        ItemSolicitacao itemEncontrado = null;

        for (ItemSolicitacao item : solicitacao.getItens()) {

            if (item.getId().equals(itemId)) {
                itemEncontrado = item;
                break;
            }
        }

        if (itemEncontrado == null) {
            throw new RuntimeException(
                    "Item não encontrado na solicitação."
            );
        }

        if (quantidade <= 0) {
            throw new RuntimeException(
                    "A quantidade deve ser maior que zero."
            );
        }

        itemEncontrado.setQuantidade(quantidade);

        validarLimites(
                solicitacao.getParametroAnual(),
                solicitacao.getItens()
        );

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public Solicitacao removerItem(
            Long itemId,
            Beneficiario beneficiario) {

        Solicitacao solicitacao =
                buscarOuCriarRascunho(beneficiario);

        ItemSolicitacao itemEncontrado = null;

        for (ItemSolicitacao item : solicitacao.getItens()) {

            if (item.getId().equals(itemId)) {
                itemEncontrado = item;
                break;
            }
        }

        if (itemEncontrado == null) {
            throw new RuntimeException(
                    "Item não encontrado na solicitação."
            );
        }

        solicitacao.getItens().remove(itemEncontrado);

        return solicitacaoRepository.save(solicitacao);
    }

    public Optional<Solicitacao> buscarSolicitacaoDoAno(Beneficiario beneficiario) {

        Integer anoAtual = Year.now().getValue();

        return solicitacaoRepository
                .findByBeneficiarioIdAndParametroAnualAno(
                        beneficiario.getId(),
                        anoAtual
                );
    }

    public List<SolicitacaoBeneficiarioResumo> listarSolicitacoesBeneficiario(
            Beneficiario beneficiario
    ) {

        List<Solicitacao> solicitacoes =
                solicitacaoRepository.findByBeneficiarioIdOrderByParametroAnualAnoDesc(
                        beneficiario.getId()
                );

        List<SolicitacaoBeneficiarioResumo> lista = new ArrayList<>();

        for (Solicitacao solicitacao : solicitacoes) {
            lista.add(new SolicitacaoBeneficiarioResumo(solicitacao));
        }

        return lista;
    }

    public ParametroAnualDisponivel calcularSaldo(
            Beneficiario beneficiario) {

        Solicitacao rascunho =
                buscarOuCriarRascunho(beneficiario);

        ParametroAnual parametro =
                rascunho.getParametroAnual();

        int totalFrutiferas = 0;
        int totalOutras = 0;

        for (ItemSolicitacao item : rascunho.getItens()) {

            if (item.getMuda().getCategoria()
                    == CategoriaMuda.FRUTIFERAS) {
                totalFrutiferas += item.getQuantidade();

            } else {
                totalOutras += item.getQuantidade();
            }
        }

        int totalGeral =
                totalFrutiferas + totalOutras;

        return new ParametroAnualDisponivel(
                totalFrutiferas,
                parametro.getLimiteFrutiferas() - totalFrutiferas,
                totalOutras,
                parametro.getLimiteOutras() - totalOutras,
                totalGeral,
                parametro.getLimiteTotalMudas() - totalGeral
        );
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