package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParametroAnualRepository extends JpaRepository<ParametroAnual, Integer> {
    List<ParametroAnual> findAllByOrderByAnoDesc();
}