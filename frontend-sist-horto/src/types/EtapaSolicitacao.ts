import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface EtapaSolicitacao {
  id: number;
  status: StatusSolicitacao;
  motivo?: string | null;
  descricao: string;
  dataHora: string;
  dataLimiteRetirada?: string | null;
}
