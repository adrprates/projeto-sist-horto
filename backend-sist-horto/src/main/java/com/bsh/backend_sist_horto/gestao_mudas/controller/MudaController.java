package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.DadosMudaResumo;
import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.service.MudaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mudas")
@CrossOrigin("*")
public class MudaController {

    private final MudaService mudaService;

    public MudaController(MudaService mudaService) {
        this.mudaService = mudaService;
    }

    @GetMapping
    public List<DadosMudaResumo> listar(MudaFilter mudaFilter) {
        return mudaService.listarResumo(mudaFilter);
    }

    @GetMapping("/{id}")
    public Muda buscarPorId(@PathVariable Long id) {
        return mudaService.getMudaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Muda criar(@RequestBody Muda muda) {
        return mudaService.salvar(muda);
    }

    @PutMapping("/{id}")
    public Muda atualizar(
            @PathVariable Long id,
            @RequestBody Muda muda) {
        return mudaService.salvar(muda);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        Muda muda = mudaService.getMudaPorId(id);
        mudaService.deletar(muda);
    }
}