package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.DadosMudaResumo;
import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Estoque;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.EstoqueRepository;
import com.bsh.backend_sist_horto.gestao_mudas.repository.MudaRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.MudaSpecification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MudaService {

    private final MudaRepository mudaRepository;
    private final EstoqueRepository estoqueRepository;
    private final EstoqueService estoqueService;

    public MudaService(MudaRepository mudaRepository,
                       EstoqueRepository estoqueRepository,
                       EstoqueService estoqueService) {
        this.mudaRepository = mudaRepository;
        this.estoqueRepository = estoqueRepository;
        this.estoqueService = estoqueService;
    }

    public List<Muda> listarTodas() {
        return mudaRepository.findAll();
    }

    public List<Muda> listarPorNomePopular(String nomePopular) {
        return mudaRepository.buscarPorNomePopular(nomePopular);
    }

    public List<DadosMudaResumo> listarResumo(MudaFilter mudaFilter) {
        List<Muda> mudas = mudaRepository.findAll(MudaSpecification.fromFilter(mudaFilter));
        Map<Long, Integer> reservasPorMuda = estoqueService.quantidadesReservadasPorMuda();

        List<DadosMudaResumo> dadosMudaResumo = new ArrayList<>();

        for(Muda muda : mudas) {
            Integer quantidadeEstoque = estoqueRepository
                    .findById(muda.getId())
                    .map(Estoque::getQuantidade)
                    .orElse(0);

            int quantidadeReservada = reservasPorMuda.getOrDefault(muda.getId(), 0);
            int quantidadeDisponivel = Math.max(quantidadeEstoque - quantidadeReservada, 0);

            dadosMudaResumo.add(new DadosMudaResumo(
                    muda.getId(),
                    muda.getNomesPopulares(),
                    muda.getCategoria(),
                    muda.getFamilia(),
                    muda.getLinkImagemArvore(),
                    quantidadeDisponivel,
                    quantidadeEstoque,
                    quantidadeReservada,
                    muda.isDisponivel(),
                    muda.getMotivoIndisponibilidade()
            ));
        }

        return dadosMudaResumo;
    }

    public Muda salvar(Muda muda) {
        boolean novaMuda = muda.getId() == null;

        if (!novaMuda) {
            mudaRepository.findById(muda.getId()).ifPresent(existente -> {
                muda.setDisponivel(existente.isDisponivel());
                muda.setMotivoIndisponibilidade(existente.getMotivoIndisponibilidade());
                muda.setEstoque(existente.getEstoque());
            });
        }

        Muda mudaSalva = mudaRepository.save(muda);

        if (novaMuda) {
            Estoque estoque = new Estoque();
            estoque.setMuda(mudaSalva);
            estoque.setQuantidade(0);

            estoqueRepository.save(estoque);
        }

        return mudaSalva;
    }

    public Muda alterarDisponibilidade(Long id, boolean disponivel, String motivo) {
        Muda muda = getMudaPorId(id);

        muda.setDisponivel(disponivel);
        muda.setMotivoIndisponibilidade(
                disponivel || motivo == null || motivo.isBlank() ? null : motivo.trim()
        );

        return mudaRepository.save(muda);
    }

    public Muda getMudaPorId(Long id) {
        Optional<Muda> mudaOptional = mudaRepository.findById(id);
        Muda muda = null;
        if (mudaOptional.isPresent()) {
            muda = mudaOptional.get();
        } else {
            throw new RuntimeException("Muda não encontrada para o id: " + id);
        }
        return muda;
    }

    public void deletar(Muda muda) {
        mudaRepository.delete(muda);
    }
}
