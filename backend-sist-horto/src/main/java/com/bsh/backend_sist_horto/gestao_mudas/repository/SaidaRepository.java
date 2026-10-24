package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.model.Distribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DistribuicaoRepository extends JpaRepository<Distribuicao, Long>, JpaSpecificationExecutor<Distribuicao> {
    boolean existsBySolicitacaoId(Long solicitationId);
}