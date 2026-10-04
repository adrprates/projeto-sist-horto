package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AtualizarPerfilRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.BeneficiarioFilter;
import com.bsh.backend_sist_horto.gestao_mudas.dto.PerfilResponse;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.record.AtualizarBeneficiarioRequest;
import com.bsh.backend_sist_horto.gestao_mudas.repository.BeneficiarioRepository;
import com.bsh.backend_sist_horto.gestao_mudas.specification.BeneficiarioSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                beneficiario.getLogin(),
                beneficiario.isSenhaProvisoria()
        );
    }

    public Beneficiario atualizarPeloAdministrador(Long id, AtualizarBeneficiarioRequest request) {
        Beneficiario beneficiario = getBeneficiarioPorId(id);

        beneficiario.setNome(request.nome());
        beneficiario.setEmail(request.email());
        beneficiario.setCelular(request.celular());
        beneficiario.setTelefone(
                request.telefone() == null || request.telefone().isBlank() ? null : request.telefone()
        );
        beneficiario.setEndereco(request.endereco());

        return beneficiarioRepository.save(beneficiario);
    }

    public Beneficiario corrigirCpf(Long id, String cpfInformado) {
        Beneficiario beneficiario = getBeneficiarioPorId(id);

        String digitosNovos = cpfInformado.replaceAll("\\D", "");

        if (digitosNovos.length() != 11) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CPF deve conter 11 dígitos"
            );
        }

        String cpfFormatado = digitosNovos.substring(0, 3) + "."
                + digitosNovos.substring(3, 6) + "."
                + digitosNovos.substring(6, 9) + "-"
                + digitosNovos.substring(9);

        String digitosAtuais = beneficiario.getCpf().replaceAll("\\D", "");

        if (digitosNovos.equals(digitosAtuais)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CPF informado é igual ao atual"
            );
        }

        if (beneficiarioRepository.existsByCpf(cpfFormatado) || beneficiarioRepository.existsByCpf(digitosNovos)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "CPF já cadastrado para outro beneficiário"
            );
        }

        if (beneficiario.getLogin().equals(digitosAtuais)) {
            if (beneficiarioRepository.existsByLogin(digitosNovos)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe um usuário com o login " + digitosNovos
                );
            }

            beneficiario.setLogin(digitosNovos);
        }

        beneficiario.setCpf(cpfFormatado);

        return beneficiarioRepository.save(beneficiario);
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

    public Beneficiario getBeneficiarioPorLogin(String login){
        Optional<Beneficiario> beneficiarioOptional = beneficiarioRepository.findByLogin(login);
        Beneficiario beneficiario = null;
        if(beneficiarioOptional.isPresent()){
            beneficiario = beneficiarioOptional.get();
        }  else {
            throw new RuntimeException("Beneficiário não encontrado para o login: " + login);
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
                beneficiario.getLogin(),
                beneficiario.isSenhaProvisoria()
        );
    }

    public void deletar(Beneficiario beneficiario){
        beneficiarioRepository.delete(beneficiario);
    }
}