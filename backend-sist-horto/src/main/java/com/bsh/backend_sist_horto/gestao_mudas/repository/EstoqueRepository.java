package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.model.Estoque;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Estoque e WHERE e.id = :idMuda")
    Optional<Estoque> buscarComBloqueio(@Param("idMuda") Long idMuda);
}
