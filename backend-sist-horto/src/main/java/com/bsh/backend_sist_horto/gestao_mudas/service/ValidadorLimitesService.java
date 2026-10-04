package com.bsh.backend_sist_horto.gestao_mudas.service;

import com.bsh.backend_sist_horto.gestao_mudas.enums.CategoriaMuda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ItemSolicitacao;
import com.bsh.backend_sist_horto.gestao_mudas.model.Muda;
import com.bsh.backend_sist_horto.gestao_mudas.model.ParametroAnual;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ValidadorLimitesService {

    public void validar(ParametroAnual parametroAnual, List<ItemSolicitacao> itensSolicitacao) {

        if (parametroAnual == null) {
            throw new RuntimeException("Parâmetro anual não definido para esta solicitação.");
        }

        int totalFrutiferas = 0;
        int totalOutras = 0;

        Map<Long, Integer> qtdPorMudaFrutifera = new HashMap<>();
        Map<Long, Integer> qtdPorMudaOutras = new HashMap<>();

        for (ItemSolicitacao item : itensSolicitacao) {
            Muda muda = item.getMuda();
            int quantidade = item.getQuantidade();

            boolean isFrutifera = muda.getCategoria() == CategoriaMuda.FRUTIFERAS;

            if (isFrutifera) {
                totalFrutiferas += quantidade;

                int qtdAtual = qtdPorMudaFrutifera.getOrDefault(muda.getId(), 0) + quantidade;
                if (qtdAtual > parametroAnual.getMaxPorEspecieFrutifera()) {
                    throw new RuntimeException(
                            "A espécie '" + muda.getNomesPopulares().get(0) + "' ultrapassa o limite máximo de " +
                                    parametroAnual.getMaxPorEspecieFrutifera() + " unidades por espécie frutífera."
                    );
                }
                qtdPorMudaFrutifera.put(muda.getId(), qtdAtual);

            } else {
                totalOutras += quantidade;

                int qtdAtual = qtdPorMudaOutras.getOrDefault(muda.getId(), 0) + quantidade;
                if (qtdAtual > parametroAnual.getMaxPorEspecieOutras()) {
                    throw new RuntimeException(
                            "A espécie '" + muda.getNomesPopulares().get(0) + "' ultrapassa o limite máximo de " +
                                    parametroAnual.getMaxPorEspecieOutras() + " unidades por espécie."
                    );
                }
                qtdPorMudaOutras.put(muda.getId(), qtdAtual);
            }
        }

        if (totalFrutiferas > parametroAnual.getLimiteFrutiferas()) {
            throw new RuntimeException(
                    "O total de mudas frutíferas solicitadas (" + totalFrutiferas +
                            ") excede o limite anual permitido (" + parametroAnual.getLimiteFrutiferas() + ")."
            );
        }

        if (totalOutras > parametroAnual.getLimiteOutras()) {
            throw new RuntimeException(
                    "O total de outras mudas solicitadas (" + totalOutras +
                            ") excede o limite anual permitido (" + parametroAnual.getLimiteOutras() + ")."
            );
        }

        int totalGeral = totalFrutiferas + totalOutras;
        if (totalGeral > parametroAnual.getLimiteTotalMudas()) {
            throw new RuntimeException(
                    "O total geral de mudas solicitadas (" + totalGeral +
                            ") excede o limite global do programa (" + parametroAnual.getLimiteTotalMudas() + ")."
            );
        }
    }
}
