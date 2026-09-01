package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.repository.MudaRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.MudaSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MudaService {

    private final MudaRepository mudaRepository;

    public MudaService(MudaRepository mudaRepository) {
        this.mudaRepository = mudaRepository;
    }

    public List<Muda> listarTodas() {
        return mudaRepository.findAll();
    }

    public List<Muda> listarPorNomePopular(String nomePopular) {
        return mudaRepository.buscarPorNomePopular(nomePopular);
    }

    public List<Muda> listarPorFiltro(MudaFilter mudaFilter) {
        return mudaRepository.findAll(MudaSpecification.fromFilter(mudaFilter));
    }

    public Muda salvar(Muda muda) {
        return mudaRepository.save(muda);
    }

    public Muda getMudaPorId(Long id) {
        Optional<Muda> mudaOptional = mudaRepository.findById(id);
        Muda muda = null;
        if (mudaOptional.isPresent()) {
            muda = mudaOptional.get();
        } else {
            throw new RuntimeException("Muda não econtrara para o id: " + id);
        }
        return muda;
    }

    public void deletar(Muda muda) {
        mudaRepository.delete(muda);
    }
}