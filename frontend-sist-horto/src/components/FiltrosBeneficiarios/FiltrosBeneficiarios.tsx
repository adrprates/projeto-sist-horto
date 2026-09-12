import { useState } from "react";
import { Search } from "lucide-react";
import type { BeneficiarioFilter } from "../../types/BeneficiarioFilter";
import { Role, rotuloRole } from "../../types/Role";
import "./FiltrosBeneficiarios.css";

type CampoBusca = "nome" | "cpf" | "email";

interface FiltrosBeneficiariosProps {
  filtro: BeneficiarioFilter;
  aoMudarFiltro: (novoFiltro: BeneficiarioFilter) => void;
}

const RESUMO_CAMPO: Record<CampoBusca, string> = {
  nome: "Nome",
  cpf: "CPF",
  email: "E-mail",
};

function FiltrosBeneficiarios({ filtro, aoMudarFiltro }: FiltrosBeneficiariosProps) {
  const [campoBusca, setCampoBusca] = useState<CampoBusca>("nome");

  function handleMudarCampoBusca(novoCampo: CampoBusca) {
    setCampoBusca(novoCampo);
    aoMudarFiltro({
      ...filtro,
      nome: undefined,
      cpf: undefined,
      email: undefined,
    });
  }

  function handleMudarValorBusca(valor: string) {
    aoMudarFiltro({ ...filtro, [campoBusca]: valor || undefined });
  }

  return (
    <div className="filtros-beneficiarios">
      <div className="filtros-linha-principal">
        <select
          className="select-campo-busca"
          value={campoBusca}
          onChange={(evento) => handleMudarCampoBusca(evento.target.value as CampoBusca)}
        >
          <option value="nome">Buscar por nome</option>
          <option value="cpf">Buscar por CPF</option>
          <option value="email">Buscar por e-mail</option>
        </select>

        <div className="campo-busca">
          <Search size={18} className="campo-busca-icone" />
          <input
            type="text"
            placeholder={`Digite o ${RESUMO_CAMPO[campoBusca].toLowerCase()}...`}
            value={filtro[campoBusca] ?? ""}
            onChange={(evento) => handleMudarValorBusca(evento.target.value)}
          />
        </div>

        <select
          className="select-role"
          value={filtro.role ?? ""}
          onChange={(evento) =>
            aoMudarFiltro({
              ...filtro,
              role: evento.target.value ? (evento.target.value as Role) : undefined,
            })
          }
        >
          <option value="">Todos os perfis</option>
          {Object.values(Role).map((role) => (
            <option key={role} value={role}>
              {rotuloRole[role]}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}

export default FiltrosBeneficiarios;