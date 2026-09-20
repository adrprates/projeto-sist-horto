export const StatusSolicitacao = {
  RASCUNHO: "RASCUNHO",
  PENDENTE: "PENDENTE",
  APROVADA: "APROVADA",
  PRONTA_PARA_RETIRADA: "PRONTA_PARA_RETIRADA",
  ENTREGUE: "ENTREGUE",
  REJEITADA: "REJEITADA"
} as const;

export type StatusSolicitacao = typeof StatusSolicitacao[keyof typeof StatusSolicitacao];

export const rotuloStatusSolicitacao: Record<StatusSolicitacao, string> = {
  [StatusSolicitacao.RASCUNHO]: "Rascunho",
  [StatusSolicitacao.PENDENTE]: "Pendente",
  [StatusSolicitacao.APROVADA]: "Aprovada",
  [StatusSolicitacao.PRONTA_PARA_RETIRADA]: "Pronta para retirada",
  [StatusSolicitacao.ENTREGUE]: "Entregue",
  [StatusSolicitacao.REJEITADA]: "Rejeitada"
};