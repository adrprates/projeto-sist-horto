import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface EtapaSolicitacao {
  id: number;
  status: StatusSolicitacao;
  descricao: string;
  dataHora: string;
  dataLimiteRetirada?: string;
}