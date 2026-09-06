import { Search, Plus } from "lucide-react";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import type { MudaFilter } from "../../types/MudaFilter";
import "./FiltrosCatalogo.css";

interface FiltrosCatalogoProps {
  filtro: MudaFilter;
  aoMudarFiltro: (novoFiltro: MudaFilter) => void;
  podeCadastrarMuda: boolean;
  aoClicarNovaMuda: () => void;
}

function FiltrosCatalogo({
  filtro,
  aoMudarFiltro,
  podeCadastrarMuda,
  aoClicarNovaMuda,
}: FiltrosCatalogoProps) {
  return (
    <div className="filtros-catalogo">
      <div className="filtros-linha-principal">
        <div className="campo-busca">
          <Search size={18} className="campo-busca-icone" />
          <input
            type="text"
            placeholder="Buscar por nome popular..."
            value={filtro.nomePopular ?? ""}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, nomePopular: evento.target.value })
            }
          />
        </div>

        {podeCadastrarMuda && (
          <button type="button" className="botao-nova-muda" onClick={aoClicarNovaMuda}>
            <Plus size={18} />
            Nova Muda
          </button>
        )}
      </div>

      <div className="filtros-linha-secundaria">
        <select
          className="select-categoria"
          value={filtro.categoria ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({
              ...filtro,
              categoria: evento.target.value
                ? (evento.target.value as CategoriaMuda)
                : undefined,
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

        <label className="filtro-checkbox">
          <input
            type="checkbox"
            checked={filtro.possuiFlores ?? false}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, possuiFlores: evento.target.checked ? true : undefined})
            }
          />
          Possui flores
        </label>

        <label className="filtro-checkbox">
          <input
            type="checkbox"
            checked={filtro.possuiFrutos ?? false}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, possuiFrutos: evento.target.checked ? true : undefined})
            }
          />
          Possui frutos
        </label>

        <label className="filtro-checkbox">
          <input
            type="checkbox"
            checked={filtro.perdeMuitasFolhas ?? false}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, perdeMuitasFolhas: evento.target.checked ? true : undefined})
            }
          />
          Perde muitas folhas
        </label>
      </div>
    </div>
  );
}

export default FiltrosCatalogo;