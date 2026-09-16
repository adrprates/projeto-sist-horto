package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import com.bsh.backend_sist_horto.gestao_mudas.service.ParametroAnualService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parametros")
@CrossOrigin("*")
public class ParametroAnualController {

    private final ParametroAnualService  parametroAnualService;

    public ParametroAnualController(ParametroAnualService parametroAnualService) {
        this.parametroAnualService = parametroAnualService;
    }

    @GetMapping
    public List<ParametroAnual> listar() {
        return parametroAnualService.listarTodos();
    }

    @GetMapping("/{id}")
    public ParametroAnual buscarPorId(@PathVariable Integer id) {
        return parametroAnualService.getParametroAnualPorAno(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParametroAnual criar(@RequestBody ParametroAnual parametroAnual){
        return parametroAnualService.salvar(parametroAnual);
    }

    @PutMapping("/{id}")
    public ParametroAnual atualizar(
            @PathVariable Integer id,
            @RequestBody ParametroAnual parametroAnual){
        return parametroAnualService.salvar(parametroAnual);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Integer id){
        ParametroAnual parametroAnual = parametroAnualService.getParametroAnualPorAno(id);
        parametroAnualService.deletar(parametroAnual);
    }
}