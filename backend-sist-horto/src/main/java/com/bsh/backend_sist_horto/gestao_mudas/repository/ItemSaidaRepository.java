package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSaida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemSaidaRepository extends JpaRepository<ItemSaida, Long>, JpaSpecificationExecutor<ItemSaida> {
}
