import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface AtualizarEtapaRequest {
  status: StatusSolicitacao;
  descricao?: string;
  dataLimiteRetirada?: string;
}