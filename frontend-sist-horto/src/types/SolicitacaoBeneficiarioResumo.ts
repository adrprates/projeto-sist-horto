import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface SolicitacaoBeneficiarioResumo {
  id: number;
  ano: number;
  statusAtual: StatusSolicitacao;
  dataSolicitacao: string;
}