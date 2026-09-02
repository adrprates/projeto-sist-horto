package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import com.bsh.backend_sist_horto.gestao_mudas.repository.ParametroAnualRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParametroAnualService {

    private final ParametroAnualRepository parametroAnualRepository;

    public ParametroAnualService(ParametroAnualRepository parametroAnualRepository) {
        this.parametroAnualRepository = parametroAnualRepository;
    }

    public List<ParametroAnual> listarTodos() {
        return this.parametroAnualRepository.findAllByOrderByAnoDesc();
    }


    public ParametroAnual salvar(ParametroAnual parametroAnual){
        return parametroAnualRepository.save(parametroAnual);
    }

    public ParametroAnual getParametroAnualPorAno(Integer ano) {
        Optional<ParametroAnual> parametroAnualOptional = parametroAnualRepository.findById(ano);
        ParametroAnual parametroAnual = null;
        if (parametroAnualOptional.isPresent()) {
            parametroAnual = parametroAnualOptional.get();
        } else {
            throw new RuntimeException("Parâmetro anual não encontrada para o ano: " + ano);
        }
        return parametroAnual;
    }

    public void deletar(ParametroAnual parametroAnual) {
        parametroAnualRepository.delete(parametroAnual);
    }
}