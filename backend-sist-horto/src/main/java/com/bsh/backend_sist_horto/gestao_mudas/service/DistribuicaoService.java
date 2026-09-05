package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.DistribuicaoFilter;
import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Distribuicao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemDistribuicao;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.repository.DistribuicaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.DistribuicaoSpecification;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DistribuicaoService {

    private final DistribuicaoRepository distribuicaoRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    private EstoqueService estoqueService;

    public DistribuicaoService(DistribuicaoRepository distribuicaoRepository,  SolicitacaoRepository solicitacaoRepository) {
        this.distribuicaoRepository = distribuicaoRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public List<Distribuicao> listarTodas() {
        return distribuicaoRepository.findAll();
    }

    public List<Distribuicao> listarPorFiltro(DistribuicaoFilter distribuicaoFilter){
        return distribuicaoRepository.findAll(DistribuicaoSpecification.fromFilter(distribuicaoFilter));
    }

    @Transactional
    public Distribuicao salvar(Distribuicao distribuicao) {
        Long solicitacaoId = distribuicao.getSolicitacao().getId();
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada para o id " + solicitacaoId));

        if (solicitacao.getStatusSolicitacao()
                != StatusSolicitacao.PRONTA_PARA_RETIRADA) {

            throw new RuntimeException(
                    "Somente solicitações prontas para retirada podem ser entregues"
            );
        }

        if (distribuicaoRepository.existsBySolicitacaoId(solicitacaoId)) {
            throw new RuntimeException(
                    "Já existe uma distribuição para esta solicitação"
            );
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

        distribuicao.setSolicitacao(solicitacao);
        distribuicao.setBeneficiario(solicitacao.getBeneficiario());
        distribuicao.setParametroAnual(solicitacao.getParametroAnual());
        distribuicao.setDataRetirada(LocalDate.now());

        for(ItemSolicitacao itemSolicitado : solicitacao.getItens()){
            ItemDistribuicao itemDistribuicao = new ItemDistribuicao();
            itemDistribuicao.setMuda(itemSolicitado.getMuda());
            itemDistribuicao.setQuantidade(itemSolicitado.getQuantidade());
            distribuicao.adicionarItem(itemDistribuicao);
            estoqueService.removerQuantidade(itemDistribuicao.getMuda().getId(), itemDistribuicao.getQuantidade());
        }

        Distribuicao distribuicaoSalva = distribuicaoRepository.save(distribuicao);

        solicitacao.setStatusSolicitacao(StatusSolicitacao.ENTREGUE);

        return distribuicaoSalva;
    }

    public Distribuicao getDistribuicaoPorId(Long id) {
        Optional<Distribuicao> distribuicaoOptional = distribuicaoRepository.findById(id);
        Distribuicao distribuicao = null;
        if (distribuicaoOptional.isPresent()) {
            distribuicao = distribuicaoOptional.get();
        } else {
            throw new RuntimeException("Distribuição não encontrada para o id: " + id);
        }
        return distribuicao;
    }

    public void deletar(Distribuicao distribuicao) {
        distribuicaoRepository.delete(distribuicao);
    }
}