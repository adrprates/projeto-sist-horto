package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarPerfilRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.BeneficiarioFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.PerfilResponse;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.BeneficiarioSpecification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository beneficiarioRepository;

    public BeneficiarioService(BeneficiarioRepository beneficiarioRepository) {
        this.beneficiarioRepository = beneficiarioRepository;
    }

    public List<Beneficiario> listarTodos() {
        return beneficiarioRepository.findAll();
    }

    public List<Beneficiario> listarPorCpf(String cpf){
        return beneficiarioRepository.findByCpfContainingIgnoreCase(cpf);
    }

    public List<Beneficiario> listarPorFiltro(BeneficiarioFilter beneficiarioFilter){
        return beneficiarioRepository.findAll(BeneficiarioSpecification.fromFilter(beneficiarioFilter));
    }

    public PerfilResponse atualizarPerfil(
            String login,
            AtualizarPerfilRequest request) {

        Beneficiario beneficiario = beneficiarioRepository
                .findByLogin(login)
                .orElseThrow(() ->
                        new RuntimeException("Beneficiário não encontrado"));

        beneficiario.setCelular(request.getCelular());
        beneficiario.setTelefone(request.getTelefone());
        beneficiario.setEmail(request.getEmail());
        beneficiario.setNome(request.getNome());
        beneficiario.setEndereco(request.getEndereco());

        beneficiarioRepository.save(beneficiario);

        return new PerfilResponse(
                beneficiario.getId(),
                beneficiario.getCpf(),
                beneficiario.getCelular(),
                beneficiario.getTelefone(),
                beneficiario.getEmail(),
                beneficiario.getNome(),
                beneficiario.getEndereco(),
                beneficiario.getLogin()
        );
    }

    public Beneficiario getBeneficiarioPorId(Long id){
        Optional<Beneficiario> beneficiarioOptional = beneficiarioRepository.findById(id);
        Beneficiario beneficiario = null;
        if(beneficiarioOptional.isPresent()){
            beneficiario = beneficiarioOptional.get();
        } else {
            throw new RuntimeException("Beneficiário não encontrado para o id: " + id);
        }
        return beneficiario;
    }

    public PerfilResponse getBeneficiarioPerfil(String login){
        Beneficiario beneficiario = beneficiarioRepository
                .findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado"));

        return new PerfilResponse(
                beneficiario.getId(),
                beneficiario.getCpf(),
                beneficiario.getCelular(),
                beneficiario.getTelefone(),
                beneficiario.getEmail(),
                beneficiario.getNome(),
                beneficiario.getEndereco(),
                beneficiario.getLogin()
        );
    }

    public void deletar(Beneficiario beneficiario){
        beneficiarioRepository.delete(beneficiario);
    }
}