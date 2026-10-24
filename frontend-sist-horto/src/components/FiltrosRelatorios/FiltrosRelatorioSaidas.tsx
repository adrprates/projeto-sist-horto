import type { SaidaFilter } from "../../types/SaidaFilter";
import type { DadosMudaResumo } from "../../types/DadosMudaResumo";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import "./FiltrosRelatorios.css";

interface FiltrosRelatorioSaidasProps {
  filtro: SaidaFilter;
  mudas: DadosMudaResumo[];
  aoMudarFiltro: (novoFiltro: SaidaFilter) => void;
  aoLimpar: () => void;
}

function FiltrosRelatorioSaidas({ filtro, mudas, aoMudarFiltro, aoLimpar }: FiltrosRelatorioSaidasProps) {
  const mudasDaCategoria = mudas
    .filter((muda) => !filtro.categoria || muda.categoria === filtro.categoria)
    .sort((a, b) => (a.nomesPopulares[0] ?? "").localeCompare(b.nomesPopulares[0] ?? ""));

  return (
    <div className="filtros-relatorio">
      <label className="filtros-relatorio-campo">
        <span>De</span>
        <input
          type="date"
          value={filtro.dataInicial ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({ ...filtro, dataInicial: evento.target.value || undefined })
          }
        />
      </label>

      <label className="filtros-relatorio-campo">
        <span>Até</span>
        <input
          type="date"
          value={filtro.dataFinal ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({ ...filtro, dataFinal: evento.target.value || undefined })
          }
        />
      </label>

      <select
        value={filtro.categoria ?? ""}
        onChange={(evento) =>
          aoMudarFiltro({
            ...filtro,
            categoria: evento.target.value ? (evento.target.value as CategoriaMuda) : undefined,
            mudaId: undefined,
          })
        }
      >
        <option value="">Todas as categorias</option>
        {Object.values(CategoriaMuda).map((categoria) => (
          <option key={categoria} value={categoria}>
            {rotuloCategoria[categoria]}
          </option>
        ))}
      </select>

      <select
        value={filtro.mudaId ?? ""}
        onChange={(evento) =>
          aoMudarFiltro({
            ...filtro,
            mudaId: evento.target.value ? Number(evento.target.value) : undefined,
          })
        }
      >
        <option value="">Todas as mudas</option>
        {mudasDaCategoria.map((muda) => (
          <option key={muda.id} value={muda.id}>
            {muda.nomesPopulares[0] ?? "Sem nome"}
          </option>
        ))}
      </select>

      <button type="button" className="filtros-relatorio-limpar" onClick={aoLimpar}>
        Limpar filtros
      </button>
    </div>
  );
}

export default FiltrosRelatorioSaidas;
