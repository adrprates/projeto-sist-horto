package com.bsh.backend_sist_horto.gestao_mudas.controller;

import com.bsh.backend_sist_horto.gestao_mudas.dto.AlterarDisponibilidadeRequest;
import com.bsh.backend_sist_horto.gestao_mudas.dto.DadosMudaResumo;
import com.bsh.backend_sist_horto.gestao_mudas.dto.MudaFilter;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.service.MudaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mudas")
@CrossOrigin("*")
@Tag(name = "Mudas")
public class MudaController {

    private final MudaService mudaService;

    public MudaController(MudaService mudaService) {
        this.mudaService = mudaService;
    }

    @GetMapping
    @SecurityRequirements
    @Operation(summary = "Lista o catálogo de mudas (público)",
            description = "Todos os filtros são opcionais e podem ser combinados. A busca por nome popular não "
                    + "diferencia maiúsculas/minúsculas e aceita trechos do nome. O estoque disponível já "
                    + "desconta as mudas reservadas por solicitações em andamento.")
    public List<DadosMudaResumo> listar(@ParameterObject MudaFilter mudaFilter) {
        return mudaService.listarResumo(mudaFilter);
    }

    @GetMapping("/{id}")
    @SecurityRequirements
    @Operation(summary = "Detalhes de uma muda (público)")
    public Muda buscarPorId(@PathVariable Long id) {
        return mudaService.getMudaPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastra uma nova muda",
            description = "O estoque da muda é criado automaticamente com quantidade zero.")
    public Muda criar(@RequestBody Muda muda) {
        muda.setId(null);
        return mudaService.salvar(muda);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita os dados de uma muda",
            description = "Disponibilidade e estoque não são alterados por este endpoint.")
    public Muda atualizar(
            @PathVariable Long id,
            @RequestBody Muda muda) {
        mudaService.getMudaPorId(id);
        muda.setId(id);
        return mudaService.salvar(muda);
    }

    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Marca a muda como disponível ou indisponível",
            description = "Ao marcar como indisponível é possível informar o motivo (máx. 255 caracteres). "
                    + "Mudas indisponíveis não podem ser adicionadas a solicitações.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidade alterada"),
            @ApiResponse(responseCode = "400", description = "Campo 'disponivel' ausente ou motivo muito longo")
    })
    public Muda alterarDisponibilidade(
            @PathVariable Long id,
            @Valid @RequestBody AlterarDisponibilidadeRequest request) {
        return mudaService.alterarDisponibilidade(
                id,
                request.getDisponivel(),
                request.getMotivo()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma muda do catálogo")
    public void deletar(@PathVariable Long id) {
        Muda muda = mudaService.getMudaPorId(id);
        mudaService.deletar(muda);
    }
}
