import { NavLink } from "react-router-dom";
import { CalendarDays, ClipboardList, ShieldCheck, Sprout, Users } from "lucide-react";
import "./PainelAdmin.css";

const LINKS_ADMIN = [
  { para: "/admin/solicitacoes", rotulo: "Gerenciar Solicitações", Icone: ClipboardList },
  { para: "/beneficiarios", rotulo: "Beneficiários", Icone: Users },
  { para: "/parametros", rotulo: "Parâmetros Anuais", Icone: CalendarDays },
  { para: "/mudas/nova", rotulo: "Cadastrar Muda", Icone: Sprout },
];

function PainelAdmin() {
  return (
    <nav className="painel-admin" aria-label="Painel do administrador">
      <span className="painel-admin-rotulo">
        <ShieldCheck size={16} />
        Administração
      </span>

      <div className="painel-admin-links">
        {LINKS_ADMIN.map(({ para, rotulo, Icone }) => (
          <NavLink
            key={para}
            to={para}
            className={({ isActive }) =>
              isActive ? "painel-admin-link painel-admin-link-ativo" : "painel-admin-link"
            }
          >
            <Icone size={16} />
            {rotulo}
          </NavLink>
        ))}
      </div>
    </nav>
  );
}

export default PainelAdmin;
