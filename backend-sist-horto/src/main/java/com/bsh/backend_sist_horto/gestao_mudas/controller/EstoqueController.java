package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.model.Estoque;
import com.bsh.backend_sist_horto.gestao_mudas.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoques")
@CrossOrigin("*")
@Tag(name = "Estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/{idMuda}")
    @Operation(summary = "Consulta o estoque físico de uma muda")
    public Estoque buscarPorId(@PathVariable Long idMuda){
        return estoqueService.getEstoquePorId(idMuda);
    }

    @PostMapping("/{idMuda}/atualizar")
    @Operation(summary = "Define a quantidade em estoque",
            description = "Substitui a quantidade atual pelo valor informado (precisa ser maior ou igual a zero).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque atualizado"),
            @ApiResponse(responseCode = "400", description = "Quantidade negativa ou muda inexistente")
    })
    public Estoque atualizarQuantidade(
            @PathVariable Long idMuda,
            @Parameter(description = "Nova quantidade (>= 0)") @RequestParam Integer quantidade) {
        return estoqueService.atualizarEstoque(idMuda, quantidade);
    }

    @PostMapping("/{idMuda}/adicionar")
    @Operation(summary = "Adiciona mudas ao estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantidade somada ao estoque"),
            @ApiResponse(responseCode = "400", description = "Quantidade menor ou igual a zero, ou muda inexistente")
    })
    public Estoque adicionarQuantidade(
            @PathVariable Long idMuda,
            @Parameter(description = "Quantidade a somar (> 0)") @RequestParam Integer quantidade) {
        return estoqueService.adicionarQuantidade(idMuda, quantidade);
    }

    @PatchMapping("/{idMuda}/remover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove mudas do estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Quantidade removida"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida ou maior que o estoque")
    })
    public void removerQuantidade(
            @PathVariable Long idMuda,
            @Parameter(description = "Quantidade a remover (> 0)") @RequestParam Integer quantidade) {
        estoqueService.removerQuantidade(idMuda, quantidade);
    }
}
