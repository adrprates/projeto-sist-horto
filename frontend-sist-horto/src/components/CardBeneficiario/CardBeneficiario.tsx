import { Mail, Phone, Smartphone, IdCard } from "lucide-react";
import type { DadosBeneficiario } from "../../types/DadosBeneficiario";
import { rotuloRole, Role } from "../../types/Role";
import "./CardBeneficiario.css";

interface CardBeneficiarioProps {
  beneficiario: DadosBeneficiario;
  aoEditar?: (beneficiario: DadosBeneficiario) => void;
}

function CardBeneficiario({ beneficiario, aoEditar }: CardBeneficiarioProps) {
  const classeBadge =
    beneficiario.role === Role.ADMINISTRADOR ? "badge-role badge-role-admin" : "badge-role";

  return (
    <div className="card-beneficiario">
      <div className="card-beneficiario-topo">
        <h3 className="card-beneficiario-nome">{beneficiario.nome ?? "Nome não informado"}</h3>
        {beneficiario.role && (
          <span className={classeBadge}>{rotuloRole[beneficiario.role]}</span>
        )}
      </div>

      <div className="card-beneficiario-info">
        {beneficiario.cpf && (
          <p>
            <IdCard size={16} />
            {beneficiario.cpf}
          </p>
        )}
        {beneficiario.email && (
          <p>
            <Mail size={16} />
            {beneficiario.email}
          </p>
        )}
        {beneficiario.celular && (
          <p>
            <Smartphone size={16} />
            {beneficiario.celular}
          </p>
        )}
        {beneficiario.telefone && (
          <p>
            <Phone size={16} />
            {beneficiario.telefone}
          </p>
        )}
      </div>

      {aoEditar && (
        <button
          type="button"
          className="botao-editar-beneficiario"
          onClick={() => aoEditar(beneficiario)}
        >
          Editar
        </button>
      )}
    </div>
  );
}

export default CardBeneficiario;