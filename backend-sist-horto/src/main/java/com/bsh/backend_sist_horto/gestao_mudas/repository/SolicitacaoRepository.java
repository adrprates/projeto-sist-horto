package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long>, JpaSpecificationExecutor<Solicitacao>{
    Optional <Solicitacao> findByBeneficiarioAndStatusAtual(Beneficiario beneficiario, StatusSolicitacao status);
    Optional <Solicitacao> findByBeneficiarioIdAndParametroAnualAno(Long idBeneficiario, Integer anoAtual);
    List<Solicitacao> findByBeneficiarioIdOrderByParametroAnualAnoDesc(
            Long beneficiarioId
    );
}