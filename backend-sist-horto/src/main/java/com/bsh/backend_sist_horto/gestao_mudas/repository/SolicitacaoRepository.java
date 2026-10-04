package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.enums.StatusSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long>, JpaSpecificationExecutor<Solicitacao>{
    Optional <Solicitacao> findByBeneficiarioAndStatusAtual(Beneficiario beneficiario, StatusSolicitacao status);
    Optional <Solicitacao> findByBeneficiarioIdAndParametroAnualAno(Long idBeneficiario, Integer anoAtual);
    List<Solicitacao> findByBeneficiarioIdOrderByParametroAnualAnoDesc(
            Long beneficiarioId
    );

    @Query("""
        SELECT COALESCE(SUM(i.quantidade), 0)
        FROM ItemSolicitacao i
        WHERE i.muda.id = :mudaId
          AND i.solicitacao.statusAtual IN :status
    """)
    Long somarQuantidadeReservada(
            @Param("mudaId") Long mudaId,
            @Param("status") Collection<StatusSolicitacao> status
    );

    @Query("""
        SELECT i.muda.id, SUM(i.quantidade)
        FROM ItemSolicitacao i
        WHERE i.solicitacao.statusAtual IN :status
        GROUP BY i.muda.id
    """)
    List<Object[]> somarQuantidadeReservadaPorMuda(
            @Param("status") Collection<StatusSolicitacao> status
    );
}
