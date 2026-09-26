import type { StatusSolicitacao } from "./StatusSolicitacao";

export interface SolicitacaoFilter {
  dataInicial?: string;
  dataFinal?: string;
  statusSolicitacao?: StatusSolicitacao;
  ano?: number;
  nomeBeneficiario?: string;
  cpfBeneficiario?: string;
}