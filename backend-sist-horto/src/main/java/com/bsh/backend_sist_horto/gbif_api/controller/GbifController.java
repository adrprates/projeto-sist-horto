package com.bsh.backend_sist_horto.gbif_api.controller;

import com.bsh.backend_sist_horto.gbif_api.dto.DadosMudaDto;
import com.bsh.backend_sist_horto.gbif_api.service.GbifService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mudas")
@CrossOrigin("*")
public class GbifController {

    private final GbifService gbifService;

    public GbifController(GbifService gbifService) {
        this.gbifService = gbifService;
    }

    @GetMapping
    public List<DadosMudaDto> listar() {
        return gbifService.listarMudas();
    }
}