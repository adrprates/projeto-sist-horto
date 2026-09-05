package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.model.Estoque;
import com.bsh.backend_sist_horto.gestao_mudas.repository.EstoqueRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
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

    public boolean verificarDisponibilidade(Long idMuda, Integer quantidade) {
        Estoque estoque = estoqueRepository.findById(idMuda).orElseThrow(() ->
                new RuntimeException(
                        "Estoque não encontrado para Muda com o id: " + idMuda
                ));

        int disponibilidade = estoque.getQuantidade() - quantidade;

        return disponibilidade >= 0;
    }
}