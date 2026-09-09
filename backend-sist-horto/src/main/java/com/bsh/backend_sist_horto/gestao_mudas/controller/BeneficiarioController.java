package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.BeneficiarioFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Beneficiario;
import com.bsh.backend_sist_horto.gestao_mudas.service.BeneficiarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beneficiarios")
@CrossOrigin("*")
public class BeneficiarioController {

    private final BeneficiarioService beneficiarioService;

    public BeneficiarioController(BeneficiarioService beneficiarioService) {
        this.beneficiarioService = beneficiarioService;
    }

    @GetMapping
    public List<Beneficiario> buscarTodos(BeneficiarioFilter beneficiarioFilter) {
        return beneficiarioService.listarPorFiltro(beneficiarioFilter);
    }

    @GetMapping("/{id}")
    public Beneficiario buscarPorId(@PathVariable Long id) {
        return beneficiarioService.getBeneficiarioPorId(id);
    }

    @PutMapping
    public Beneficiario atualizar(
            @PathVariable Long id,
            @RequestBody Beneficiario beneficiario
    ){
        return beneficiarioService.atualizar(id, beneficiario);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        Beneficiario beneficiario = buscarPorId(id);
        beneficiarioService.deletar(beneficiario);
    }
}