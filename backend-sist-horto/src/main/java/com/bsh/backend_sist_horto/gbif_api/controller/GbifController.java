package com.bsh.backend_sist_horto.gbif_api.controller;

import com.bsh.backend_sist_horto.gbif_api.dto.DadosMudaDto;
import com.bsh.backend_sist_horto.gbif_api.service.GbifService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("gbif/mudas")
@CrossOrigin("*")
@Tag(name = "GBIF")
public class GbifController {

    private final GbifService gbifService;

    public GbifController(GbifService gbifService) {
        this.gbifService = gbifService;
    }

    @GetMapping
    @Operation(summary = "Busca dados taxonômicos e imagens das mudas base no GBIF",
            description = "Consulta a API pública do GBIF (Global Biodiversity Information Facility).")
    public List<DadosMudaDto> listar() {
        return gbifService.listarMudas();
    }
}
