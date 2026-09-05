package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.service.MudaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mudas")
public class MudaController {

    private final MudaService mudaService;

    public MudaController(MudaService mudaService) {
        this.mudaService = mudaService;
    }

    @GetMapping
    public List<Muda> listarTodas(){
        return mudaService.listarTodas();
    }

    @PostMapping("/filtro")
    public List<Muda> listarPorFiltro(
            @RequestBody MudaFilter filtro) {

        return mudaService.listarPorFiltro(filtro);
    }

    @PostMapping
    public Muda salvar(@RequestBody Muda muda) {
        return mudaService.salvar(muda);
    }

    @PutMapping("/{id}")
    public Muda atualizar(
            @PathVariable Long id,
            @RequestBody Muda muda) {

        muda.setId(id);

        return mudaService.salvar(muda);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {

        Muda muda = mudaService.getMudaPorId(id);

        mudaService.deletar(muda);
    }
}