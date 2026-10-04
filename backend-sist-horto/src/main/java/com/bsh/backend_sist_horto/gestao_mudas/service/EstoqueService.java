package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Estoque;
import com.bsh.backend_sist_horto.gestao_mudas.repository.EstoqueRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.SolicitacaoRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class EstoqueService {

    public static final Set<StatusSolicitacao> STATUS_QUE_RESERVAM_ESTOQUE = EnumSet.of(
            StatusSolicitacao.PENDENTE,
            StatusSolicitacao.APROVADA,
            StatusSolicitacao.AGUARDANDO_CONFIRMACAO,
            StatusSolicitacao.PRONTA_PARA_RETIRADA
    );

    private final EstoqueRepository estoqueRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public EstoqueService(EstoqueRepository estoqueRepository, SolicitacaoRepository solicitacaoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public Estoque atualizarEstoque(Long idMuda, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(idMuda).orElseThrow(() ->
                new RuntimeException(
                        "Estoque não encontrado para Muda com o id: " + idMuda
                ));
        if(quantidade < 0){
            throw new RuntimeException(
                    "Quantidade precisa ser igual ou maior que 0"
            );
        }
        estoque.setQuantidade(quantidade);
        return estoqueRepository.save(estoque);
    }

    public Estoque adicionarQuantidade(Long idMuda, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(idMuda).orElseThrow(() ->
                        new RuntimeException(
                                "Estoque não encontrado para Muda com o id: " + idMuda
                        ));
        if (quantidade <= 0) {
            throw new RuntimeException(
                    "Quantidade deve ser maior que zero"
            );
        }
        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        return estoqueRepository.save(estoque);
    }

    public void removerQuantidade(Long idMuda, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(idMuda).orElseThrow(() ->
                new RuntimeException(
                        "Estoque não encontrado para Muda com o id: " + idMuda
                ));

        if (quantidade <= 0) {
            throw new RuntimeException(
                    "Quantidade deve ser maior que zero"
            );
        }
        int novaQuantidade = estoque.getQuantidade() -  quantidade;
        if(novaQuantidade < 0) {
            throw new RuntimeException(
                    "Quantidade insuficiente em estoque"
            );
        }
        estoque.setQuantidade(novaQuantidade);
        estoqueRepository.save(estoque);
    }

    public Estoque getEstoquePorId(Long idMuda) {
        Optional<Estoque> estoqueOptional = estoqueRepository.findById(idMuda);
        Estoque estoque = null;
        if (estoqueOptional.isPresent()) {
            estoque = estoqueOptional.get();
        } else {
            throw new RuntimeException("Estoque não encontrado para Muda com o id: " + idMuda);
        }
        return estoque;
    }

    public boolean verificarDisponibilidade(Long idMuda, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(idMuda).orElseThrow(() ->
                new RuntimeException(
                        "Estoque não encontrado para Muda com o id: " + idMuda
                ));

        return calcularDisponivel(estoque) >= quantidade;
    }

    public boolean verificarDisponibilidadeComBloqueio(Long idMuda, Integer quantidade) {
        Estoque estoque = estoqueRepository.buscarComBloqueio(idMuda).orElseThrow(() ->
                new RuntimeException(
                        "Estoque não encontrado para Muda com o id: " + idMuda
                ));

        return calcularDisponivel(estoque) >= quantidade;
    }

    public int quantidadeReservada(Long idMuda) {
        return solicitacaoRepository
                .somarQuantidadeReservada(idMuda, STATUS_QUE_RESERVAM_ESTOQUE)
                .intValue();
    }

    public int quantidadeDisponivel(Long idMuda) {
        return estoqueRepository.findById(idMuda)
                .map(this::calcularDisponivel)
                .orElse(0);
    }

    public Map<Long, Integer> quantidadesReservadasPorMuda() {
        Map<Long, Integer> reservas = new HashMap<>();

        for (Object[] linha : solicitacaoRepository.somarQuantidadeReservadaPorMuda(STATUS_QUE_RESERVAM_ESTOQUE)) {
            reservas.put((Long) linha[0], ((Number) linha[1]).intValue());
        }

        return reservas;
    }

    private int calcularDisponivel(Estoque estoque) {
        return Math.max(estoque.getQuantidade() - quantidadeReservada(estoque.getId()), 0);
    }
}