import type { RelatorioEstoqueFilter } from "../../types/RelatorioEstoqueFilter";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import { SituacaoEstoque, rotuloSituacaoEstoque } from "../../types/SituacaoEstoque";
import "./FiltrosRelatorios.css";

interface FiltrosRelatorioEstoqueProps {
  filtro: RelatorioEstoqueFilter;
  aoMudarFiltro: (novoFiltro: RelatorioEstoqueFilter) => void;
  aoLimpar: () => void;
}

function FiltrosRelatorioEstoque({ filtro, aoMudarFiltro, aoLimpar }: FiltrosRelatorioEstoqueProps) {
  return (
    <div className="filtros-relatorio">
      <select
        value={filtro.categoria ?? ""}
        onChange={(evento) =>
          aoMudarFiltro({
            ...filtro,
            categoria: evento.target.value ? (evento.target.value as CategoriaMuda) : undefined,
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
        value={filtro.situacao ?? ""}
        onChange={(evento) =>
          aoMudarFiltro({
            ...filtro,
            situacao: evento.target.value ? (evento.target.value as SituacaoEstoque) : undefined,
          })
        }
      >
        <option value="">Todas as situações</option>
        {Object.values(SituacaoEstoque).map((situacao) => (
          <option key={situacao} value={situacao}>
            {rotuloSituacaoEstoque[situacao]}
          </option>
        ))}
      </select>

      <button type="button" className="filtros-relatorio-limpar" onClick={aoLimpar}>
        Limpar filtros
      </button>
    </div>
  );
}

export default FiltrosRelatorioEstoque;
