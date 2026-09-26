import { Search } from "lucide-react";
import type { SolicitacaoFilter } from "../../types/SolicitacaoFilter";
import { StatusSolicitacao, rotuloStatusSolicitacao } from "../../types/StatusSolicitacao";
import "./FiltrosSolicitacoesAdmin.css";

interface FiltrosSolicitacoesAdminProps {
  filtro: SolicitacaoFilter;
  aoMudarFiltro: (novoFiltro: SolicitacaoFilter) => void;
}

function FiltrosSolicitacoesAdmin({ filtro, aoMudarFiltro }: FiltrosSolicitacoesAdminProps) {
  return (
    <div className="filtros-solicitacoes">
      <div className="filtros-linha">
        <div className="campo-busca">
          <Search size={18} className="campo-busca-icone" />
          <input
            type="text"
            placeholder="Buscar por nome do beneficiário..."
            value={filtro.nomeBeneficiario ?? ""}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, nomeBeneficiario: evento.target.value || undefined })
            }
          />
        </div>

        <input
          type="text"
          className="campo-texto"
          placeholder="CPF do beneficiário"
          value={filtro.cpfBeneficiario ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({ ...filtro, cpfBeneficiario: evento.target.value || undefined })
          }
        />
      </div>


      <div className="filtros-linha">
        <select

          className="campo-select"
          value={filtro.statusSolicitacao ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({
              ...filtro,
              statusSolicitacao: evento.target.value
                ? (evento.target.value as StatusSolicitacao)
                : undefined,
            })
          }
        >
          <option value="">Todos os status</option>
          
          {Object.values(StatusSolicitacao)
            .filter((status) => status !== StatusSolicitacao.RASCUNHO)
            .map((status) => (
              <option key={status} value={status}>
                {rotuloStatusSolicitacao[status]}
              </option>
            ))}
        </select>

        <input
          type="number"
          className="campo-texto campo-ano"
          placeholder="Ano"
          value={filtro.ano ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({
              ...filtro,
              ano: evento.target.value ? Number(evento.target.value) : undefined,
            })
          }
        />

        <label className="campo-data">
          <span>De</span>
          <input
            type="date"
            value={filtro.dataInicial ?? ""}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, dataInicial: evento.target.value || undefined })
            }
          />
        </label>

        <label className="campo-data">
          <span>Até</span>
          <input
            type="date"
            value={filtro.dataFinal ?? ""}
            onChange={(evento) =>
              aoMudarFiltro({ ...filtro, dataFinal: evento.target.value || undefined })
            }
          />
        </label>
      </div>
    </div>
  );
}

export default FiltrosSolicitacoesAdmin;