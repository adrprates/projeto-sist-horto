package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.SolicitacaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.ParametroAnualRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.SolicitacaoSpecification;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ParametroAnualRepository parametroAnualRepository;

    private EstoqueService estoqueService;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository,  ParametroAnualRepository parametroAnualRepository, EstoqueService estoqueService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.parametroAnualRepository = parametroAnualRepository;
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public List<Solicitacao> listarPorFiltro(SolicitacaoFilter solicitacaoFilter) {
        return solicitacaoRepository.findAll(SolicitacaoSpecification.fromFilter(solicitacaoFilter));
    }

    @Transactional
    public Solicitacao salvar(Solicitacao solicitacao, List<ItemSolicitacao> itensSolicitacao) {

        if (itensSolicitacao != null && !itensSolicitacao.isEmpty()) {
            for (ItemSolicitacao itemSolicitado : itensSolicitacao) {
                solicitacao.adicionarItem(itemSolicitado);
            }
        }

        List<String> mudasEmFalta = new ArrayList<>();

        for(ItemSolicitacao itemSolicitado : solicitacao.getItens()){
            Long mudaId = itemSolicitado.getMuda().getId();
            Integer quantidadeSolicitada = itemSolicitado.getQuantidade();

            boolean possuiSaldo = estoqueService.verificarDisponibilidade(mudaId, quantidadeSolicitada);

            if(!possuiSaldo){
                String nomeMuda = itemSolicitado.getMuda().getNomesPopulares().get(0);
                mudasEmFalta.add(
                        nomeMuda + " (" +
                                quantidadeSolicitada + " solicitadas)"
                );
            }
        }

        if(!mudasEmFalta.isEmpty()){
            throw new RuntimeException(
                    "Estoque insuficiente para: " +
                            String.join(", ", mudasEmFalta)
            );
        }

        Integer anoAtual = LocalDate.now().getYear();

        ParametroAnual parametroAnual =
                parametroAnualRepository.findById(anoAtual)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Parâmetro anual não cadastrado para o ano " + anoAtual
                                )
                        );

        validarLimites(parametroAnual, solicitacao.getItens());

        solicitacao.setParametroAnual(parametroAnual);
        solicitacao.setDataSolicitacao(LocalDate.now());
        solicitacao.setStatusSolicitacao(StatusSolicitacao.PENDENTE);

        return solicitacaoRepository.save(solicitacao);
    }

    @Transactional
    public void atualizarStatus(Long idSolicitacao, StatusSolicitacao statusSolicitacao) {
        Solicitacao solicitacao = getSolicitacaoPorId(idSolicitacao);
        solicitacao.setStatusSolicitacao(statusSolicitacao);
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