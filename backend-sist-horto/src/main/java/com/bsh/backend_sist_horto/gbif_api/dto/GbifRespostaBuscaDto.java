package com.bsh.backend_sist_horto.gbif_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GbifRespostaBuscaDto {

    private Integer usageKey;
    private String canonicalName;
    private String kingdom;
    private String phylum;
    private String order;
    private String family;
    private String genus;
    private String species;

    @JsonProperty("class")
    private String clazz;
}