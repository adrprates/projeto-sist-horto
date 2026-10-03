import { Search } from "lucide-react";
import { CategoriaMuda, rotuloCategoria } from "../../types/CategoriaMuda";
import type { MudaFilter } from "../../types/MudaFilter";
import "./FiltrosCatalogo.css";

interface FiltrosCatalogoProps {
  filtro: MudaFilter;
  aoMudarFiltro: (novoFiltro: MudaFilter) => void;
}

function FiltrosCatalogo({ filtro, aoMudarFiltro }: FiltrosCatalogoProps) {
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
      </div>

      <div className="filtros-linha-secundaria">
        <div className="filtro-categorias" role="group" aria-label="Filtrar por categoria">
          <button
            type="button"
            className={!filtro.categoria ? "filtro-pilula filtro-pilula-ativa" : "filtro-pilula"}
            aria-pressed={!filtro.categoria}
            onClick={() => aoMudarFiltro({ ...filtro, categoria: undefined })}
          >
            Todas
          </button>
          {Object.values(CategoriaMuda).map((categoria) => (
            <button
              key={categoria}
              type="button"
              className={
                filtro.categoria === categoria ? "filtro-pilula filtro-pilula-ativa" : "filtro-pilula"
              }
              aria-pressed={filtro.categoria === categoria}
              onClick={() => aoMudarFiltro({ ...filtro, categoria })}
            >
              {rotuloCategoria[categoria]}
            </button>
          ))}
        </div>
      </div>

      <div className="filtros-linha-caracteristicas">
        <span className="filtros-rotulo">Características</span>
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