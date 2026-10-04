import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface ItemPropostoRequest {
  mudaId: number;
  quantidade: number;
}

export interface AtualizarEtapaRequest {
  status: StatusSolicitacao;
  motivo?: string;
  descricao?: string;
  dataLimiteRetirada?: string;
  itensPropostos?: ItemPropostoRequest[];
}
