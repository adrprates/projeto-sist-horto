package com.bsh.backend_sist_horto.gestao_mudas.repository;

import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long>, JpaSpecificationExecutor<Beneficiario> {
    List<Beneficiario> findByCpfContainingIgnoreCase(String cpf);
}