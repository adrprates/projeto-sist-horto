package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MudaRepository extends JpaRepository<Muda, Long>, JpaSpecificationExecutor<Muda> {
    @Query("""
        SELECT DISTINCT m
        FROM Muda m
        JOIN m.nomesPopulares np
        WHERE LOWER(np) LIKE LOWER(CONCAT('%', :nomePopular, '%'))
    """)
    List<Muda> buscarPorNomePopular(@Param("nomePopular") String nomePopular);
}