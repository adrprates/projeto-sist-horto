package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.model.Estoque;
import com.bsh.backend_sist_horto.gestao_mudas.service.EstoqueService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoques")
@CrossOrigin("*")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/{idMuda}")
    public Estoque buscarPorId(@PathVariable Long idMuda){
        return estoqueService.getEstoquePorId(idMuda);
    }

    @PostMapping("/{idMuda}/atualizar")
    public Estoque atualizarQuantidade(@PathVariable Long idMuda, @RequestParam Integer quantidade) {
        return estoqueService.atualizarEstoque(idMuda, quantidade);
    }

    @PostMapping("/{idMuda}/adicionar")
    public Estoque adicionarQuantidade(@PathVariable Long idMuda, @RequestParam Integer quantidade) {
        return estoqueService.adicionarQuantidade(idMuda, quantidade);
    }

    @PatchMapping("/{idMuda}/remover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerQuantidade(
            @PathVariable Long idMuda,
            @RequestParam Integer quantidade) {

        estoqueService.removerQuantidade(idMuda, quantidade);
    }
}