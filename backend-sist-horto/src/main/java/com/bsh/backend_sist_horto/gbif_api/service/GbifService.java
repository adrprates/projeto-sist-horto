package com.bsh.backend_sist_horto.service;

import com.bsh.backend_sist_horto.dto.DadosMudaDto;
import com.bsh.backend_sist_horto.dto.GbifRespostaBuscaDto;
import com.bsh.backend_sist_horto.dto.GbifRespostaMidiaDto;
import com.bsh.backend_sist_horto.dto.MudaBaseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GbifService {

    private final RestClient restClient;

    public GbifService(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder.baseUrl("https://api.gbif.org").build();
    }

    private static final List<MudaBaseDto> MUDAS = List.of(
            new MudaBaseDto("Amora", "Morus nigra"),
//            new MudaBaseDto("Ameixa Amarela", "Prunus salicina"),
//            new MudaBaseDto("Cajamanga Grande", "Spondias dulcis"),
            new MudaBaseDto("Figo Grande", "Ficus carica"),
//            new MudaBaseDto("Goiaba", "Psidium guajava"),
            new MudaBaseDto("Tamarindo", "Tamarindus indica"),
//            new MudaBaseDto("Romã", "Punica granatum"),
//            new MudaBaseDto("Uvaia", "Eugenia pyriformis"),
            new MudaBaseDto("Manga Comum", "Mangifera indica"),
//            new MudaBaseDto("Guapeva", "Pouteria gardneriana"),
//            new MudaBaseDto("Pitaia Branca", "Hylocereus undatus"),
//            new MudaBaseDto("Pitaia Amarela", "Selenicereus megalanthus"),
//            new MudaBaseDto("Pitaia Vermelha", "Hylocereus costaricensis"),
//            new MudaBaseDto("Maracujá", "Passiflora edulis"),
            new MudaBaseDto("Flamboyant", "Delonix regia")
//            new MudaBaseDto("Oiti", "Licania tomentosa"),
//            new MudaBaseDto("Pata de Vaca Roxa", "Bauhinia purpurea"),
//            new MudaBaseDto("Pata de Vaca Branca", "Bauhinia variegata"),
//            new MudaBaseDto("Aroeira Pimenteira", "Schinus terebinthifolia"),
//            new MudaBaseDto("Ingá", "Inga edulis"),
//            new MudaBaseDto("Saboneteiro", "Sapindus saponaria"),
//            new MudaBaseDto("Urucum Anão", "Bixa orellana"),
//            new MudaBaseDto("Jasmim Manga", "Plumeria rubra")
    );

    public GbifRespostaBuscaDto buscarDadosEspecie(String nomeCientifico){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/species/match")
                        .queryParam("name", nomeCientifico)
                        .build())
                .retrieve()
                .body(GbifRespostaBuscaDto.class);
    }

    public GbifRespostaMidiaDto buscarMidias(Integer usageKey) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/species/{usageKey}/media")
                        .build(usageKey))
                .retrieve()
                .body(GbifRespostaMidiaDto.class);

    }

    public List<DadosMudaDto> listarMudas(){

        List<DadosMudaDto> listaMudas = new ArrayList<>();

        for(MudaBaseDto muda : MUDAS){
            GbifRespostaBuscaDto especie = buscarDadosEspecie(muda.getNomeCanonico());

            if (especie.getUsageKey() == null) {
                continue;
            }

            GbifRespostaMidiaDto midia = buscarMidias(especie.getUsageKey());

            DadosMudaDto dadosMudaDto = new DadosMudaDto();

            dadosMudaDto.setNomePopular(muda.getNomePopular());
            dadosMudaDto.setNomeCanonico(especie.getCanonicalName());

            if (!midia.getResults().isEmpty()) {
                dadosMudaDto.setUrlImagem(
                        midia.getResults().get(0).getIdentifier()
                );
            }

            dadosMudaDto.setReino(especie.getKingdom());
            dadosMudaDto.setFilo(especie.getPhylum());
            dadosMudaDto.setClasse(especie.getClazz());
            dadosMudaDto.setOrdem(especie.getOrder());
            dadosMudaDto.setFamilia(especie.getFamily());
            dadosMudaDto.setGenero(especie.getGenus());
            dadosMudaDto.setEspecie(especie.getSpecies());

            listaMudas.add(dadosMudaDto);
        }

        return listaMudas;
    }
}