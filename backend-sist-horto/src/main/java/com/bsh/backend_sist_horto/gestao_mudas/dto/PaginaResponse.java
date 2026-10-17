package com.bsh.backend_sist_horto.gestao_mudas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class PaginaResponse<T> {
    private List<T> conteudo;
    private int pagina;
    private int tamanho;
    private long totalElementos;
    private int totalPaginas;

    public static <T> PaginaResponse<T> de(Page<T> pagina) {
        return new PaginaResponse<>(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages()
        );
    }
}
