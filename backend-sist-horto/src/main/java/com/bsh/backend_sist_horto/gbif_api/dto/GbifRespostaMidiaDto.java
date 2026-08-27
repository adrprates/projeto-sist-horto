package com.bsh.backend_sist_horto.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GbifRespostaMidiaDto {

    private List<GbifMidiaDto> results;
}