package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import com.bsh.backend_sist_horto.gestao_mudas.service.ParametroAnualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parametros")
@CrossOrigin("*")
@Tag(name = "Parâmetros Anuais")
public class ParametroAnualController {

    private final ParametroAnualService  parametroAnualService;

    public ParametroAnualController(ParametroAnualService parametroAnualService) {
        this.parametroAnualService = parametroAnualService;
    }

    @GetMapping
    @Operation(summary = "Lista os parâmetros de todos os anos (mais recente primeiro)")
    public List<ParametroAnual> listar() {
        return parametroAnualService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca os parâmetros de um ano")
    public ParametroAnual buscarPorId(@Parameter(description = "Ano (ex.: 2026)") @PathVariable Integer id) {
        return parametroAnualService.getParametroAnualPorAno(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra os limites de um ano",
            description = "Define o limite total de mudas por pessoa, o limite de frutíferas, o limite das demais "
                    + "espécies e o máximo por espécie. Valores não podem ser negativos.")
    public ParametroAnual criar(@RequestBody ParametroAnual parametroAnual){
        return parametroAnualService.salvar(parametroAnual);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os limites de um ano")
    public ParametroAnual atualizar(
            @Parameter(description = "Ano (ex.: 2026)") @PathVariable Integer id,
            @RequestBody ParametroAnual parametroAnual){
        parametroAnualService.getParametroAnualPorAno(id);
        parametroAnual.setAno(id);
        return parametroAnualService.salvar(parametroAnual);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove os parâmetros de um ano")
    public void deletar(@Parameter(description = "Ano (ex.: 2026)") @PathVariable Integer id){
        ParametroAnual parametroAnual = parametroAnualService.getParametroAnualPorAno(id);
        parametroAnualService.deletar(parametroAnual);
    }
}
