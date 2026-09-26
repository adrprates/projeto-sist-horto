import type { StatusSolicitacao } from "../../types/StatusSolicitacao";
import { rotuloStatusSolicitacao } from "../../types/StatusSolicitacao";
import "./BadgeStatusSolicitacao.css";

interface BadgeStatusSolicitacaoProps {
  status: StatusSolicitacao;
}

const CLASSE_POR_STATUS: Record<StatusSolicitacao, string> = {
  RASCUNHO: "badge-status-cinza",
  PENDENTE: "badge-status-amarelo",
  APROVADA: "badge-status-verde",
  AGUARDANDO_CONFIRMACAO: "badge-status-amarelo",
  PRONTA_PARA_RETIRADA: "badge-status-verde",
  ENTREGUE: "badge-status-verde-escuro",
  REJEITADA: "badge-status-vermelho",
  EXPIRADA: "badge-status-vermelho",
};

function BadgeStatusSolicitacao({ status }: BadgeStatusSolicitacaoProps) {
  return (
    <span className={`badge-status ${CLASSE_POR_STATUS[status]}`}>
      {rotuloStatusSolicitacao[status]}
    </span>
  );
}

export default BadgeStatusSolicitacao;